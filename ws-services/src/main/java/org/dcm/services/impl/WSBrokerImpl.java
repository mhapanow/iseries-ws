package org.dcm.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dcm.services.exception.DCMException;
import org.dcm.services.exception.DCMExceptionHelper;
import org.dcm.services.model.FieldDescriptor;
import org.dcm.services.model.RecordDescriptor;
import org.dcm.services.model.WSDescriptor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.ibm.as400.access.DataQueue;
import com.ibm.as400.access.DataQueueEntry;
import com.ibm.as400.access.IFSFileInputStream;
import com.ibm.as400.access.KeyedDataQueue;
import com.inodes.util.CollectionFactory;

public class WSBrokerImpl {

	private static final Logger log = Logger.getLogger(WSBrokerImpl.class.getName());
	private static Map<String, WSDescriptor> wsCache;
	private static final Semaphore sem = new Semaphore(1);
	
	@Autowired
	private ConnectionHelperImpl conn;
	
	public synchronized static WSDescriptor getCachedService(String serviceName) {
		try {
			sem.acquire();
			
			if( wsCache == null )
				wsCache = CollectionFactory.createMap();
			
			return wsCache.get(serviceName);
			
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return null;
		} finally { 
			sem.release();
		}
	}
	
	public synchronized static void setCachedService(String serviceName, WSDescriptor descriptor) {
		try {
			sem.acquire();
			
			if( wsCache == null )
				wsCache = CollectionFactory.createMap();
			
			wsCache.put(serviceName, descriptor);
			
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
		} finally { 
			sem.release();
		}
	}
	
	public void startConsumer() throws DCMException {
		
		try {

			DataQueue inputDQ = conn.getInputDataQueue();
			KeyedDataQueue outputDQ = conn.getOutputDataQueue();
			RecordDescriptor inputFormat = getInputRecordDescriptor();
			RecordDescriptor outputFormat = getOutputRecordDescriptor();

			log.log(Level.INFO, "Waiting for record...");
			DataQueueEntry DQData = inputDQ.read(-1);
			while( true ) {

				// We just read an entry off the queue.  Put the data into
                // a record object so the program can access the fields of
                // the data by name.  The Record object will also convert
                // the data from server format to Java format.
				String data = DQData.getString();
				System.out.println(data);
				JSONObject json = RecordHelper.toJson(inputFormat, data);
				
                String serviceName = json.getString("serviceName");
                String key = json.getString("key");
                String parameters = json.getString("parameters");

                log.log(Level.INFO, "Message received for service " + serviceName.trim() + " and key " + key.trim());
                log.log(Level.INFO, "Parameters: " + parameters.trim());
        		JSONObject response = new JSONObject();

        		try {
        			WSDescriptor ws = getWSUsingName(serviceName.trim());
        			log.log(Level.INFO, "Web Service definition is " + ws.toString());

        			// Executes the actual step
        			StringBuffer stdout = new StringBuffer();
        			StringBuffer stderr = new StringBuffer();
        			StringBuffer commandLine = new StringBuffer();
        			Map<String, Object> parsedParameters = RecordHelper.toMap(ws.getInputRecordDescriptor(), parameters); 
//        			int rcode = executeCurlOutgoingService(ws, parsedParameters, stdout, stderr, commandLine);
        			int rcode = executeHttpOutgoingService(ws, parsedParameters, stdout, stderr, commandLine);

        			if( rcode == 0 || rcode == 200 ) {
        				String responseString = parseResponse(ws.getOutputRecordDescriptor(), stdout.toString());
        				response.put("responseCode", "WSR0200");
        				response.put("responseText", responseString);
        			} else {
        				response.put("responseCode", "WSR0500");
        				response.put("responseText", "");
        			}
        		} catch( Exception e ) {
        			log.log(Level.SEVERE, e.getMessage(), e);
        			response.put("responseCode", "WSR0500");
        			response.put("responseText", e.getMessage());
        		}

                String outputData = RecordHelper.fromJson(outputFormat, response);
                log.log(Level.INFO, "Sending response to key " + key.trim() + " with data " + response.toString());
                outputDQ.write(key, outputData.trim());
                
                // Wait for the next entry.
    			log.log(Level.INFO, "Waiting for record...");
                DQData = inputDQ.read(-1);
			}
			
		} catch( Exception e ) {
			throw DCMExceptionHelper.defaultException(e.getMessage(), e);
		}
		
	}

	/**
	 * Executes a server side Web Service
	 * 
	 * @param ws
	 *            The Web Service definition to execute
	 * @param parameters
	 *            The received parameter list
	 * @return The Received Response
	 * @throws DCMException
	 */
	public JSONObject executeServerWS(WSDescriptor ws, JSONObject parameters) throws DCMException {

		try {
			KeyedDataQueue inputDQ = conn.getServerInputDataQueue();
			KeyedDataQueue outputDQ = conn.getServerOutputDataQueue();
			RecordDescriptor inputFormat = getServerInputRecordDescriptor();
			RecordDescriptor outputFormat = getServerOutputRecordDescriptor();
			String key = UUID.randomUUID().toString();
			String general = "*GENERAL  ";
			
			JSONObject input = new JSONObject();
			String inputRecord = RecordHelper.fromJson(ws.getInputRecordDescriptor(), parameters);
			input.put("serviceName", ws.getName());
			input.put("key", key);
			input.put("parameters", inputRecord);
			String inputData = RecordHelper.fromJson(inputFormat, input);

			log.log(Level.INFO, "Sending request for key " + key.trim() + " to service " + ws.getName());
			inputDQ.write(general, inputData.trim());

			log.log(Level.INFO, "Waiting for response for key " + key.trim() + "...");
			DataQueueEntry DQData = outputDQ.read(key, -1, "EQ");
			String data = DQData.getString();
			System.out.println(data);
			JSONObject json = RecordHelper.toJson(outputFormat, data);

			log.log(Level.INFO, "Message received for service " + ws.getName() + " and key " + key.trim() + ": " + json.getString("responseCode"));
			log.log(Level.INFO, json.getString("responseText"));

			if(json.getString("responseCode").equals("WSR0200")) {
				return RecordHelper.toJson(ws.getOutputRecordDescriptor(), json.getString("responseText"));
			} else {
				throw DCMExceptionHelper.defaultException(json.getString("responseCode"), new Exception());
			}
		} catch( Exception e ) {
			if( e instanceof DCMException ) throw (DCMException)e;
			else {
				throw DCMExceptionHelper.defaultException(e.getMessage(), e);
			}
		}
	}

	/**
	 * Returns the input record format for a WS Message
	 * 
	 * @return
	 */
	private static RecordDescriptor getInputRecordDescriptor() {
		RecordDescriptor rd = new RecordDescriptor("WSI", "Input Record Descriptor");
		rd.getFields().add(new FieldDescriptor("DFNAME", "Service Name", "serviceName", "CHAR", 10, 0));
		rd.getFields().add(new FieldDescriptor("DFKEY", "Response Key", "key", "CHAR", 26, 0));
		rd.getFields().add(new FieldDescriptor("DFPARM", "Service Parameters", "parameters", "CHAR", 32000, 0));
		return rd;
	}

	/**
	 * Returns the input record format for a WS Message
	 * 
	 * @return
	 */
	private static RecordDescriptor getOutputRecordDescriptor() {
		RecordDescriptor rd = new RecordDescriptor("WSO", "Output Record Descriptor");
		rd.getFields().add(new FieldDescriptor("DFCODE", "Response Code", "responseCode", "CHAR", 7, 0));
		rd.getFields().add(new FieldDescriptor("DFPARM", "Service Parameters", "responseText", "CHAR", 32000, 0));
		return rd;
	}

	/**
	 * Returns the input record format for a WS Message
	 * 
	 * @return
	 */
	private static RecordDescriptor getServerInputRecordDescriptor() {
		RecordDescriptor rd = new RecordDescriptor("WSIS", "Server Input Record Descriptor");
		rd.getFields().add(new FieldDescriptor("DFNAME", "Service Name", "serviceName", "CHAR", 10, 0));
		rd.getFields().add(new FieldDescriptor("DFKEY", "Response Key", "key", "CHAR", 36, 0));
		rd.getFields().add(new FieldDescriptor("DFPARM", "Service Parameters", "parameters", "CHAR", 32000, 0));
		return rd;
	}

	/**
	 * Returns the input record format for a WS Message
	 * 
	 * @return
	 */
	private static RecordDescriptor getServerOutputRecordDescriptor() {
		RecordDescriptor rd = new RecordDescriptor("WSOS", "Server Output Record Descriptor");
		rd.getFields().add(new FieldDescriptor("DFCODE", "Response Code", "responseCode", "CHAR", 7, 0));
		rd.getFields().add(new FieldDescriptor("DFPARM", "Service Parameters", "responseText", "CHAR", 32000, 0));
		return rd;
	}

	/**
	 * Builds the Web Service Description based on the requested service name
	 * 
	 * @param name
	 *            The service name
	 * @return A fully formed WSDescriptor
	 * @throws DCMException
	 */
	public WSDescriptor getWSUsingName(String name) throws DCMException {

		if(!conn.isConnected())
			conn.connect();

		Statement stmtObject = null;
		ResultSet rs = null;
		SimpleDateFormat rsdf = new SimpleDateFormat("yyyyMMdd HHmmss");
		DecimalFormat df8 = new DecimalFormat("00000000");
		DecimalFormat df6 = new DecimalFormat("000000");

		try {
			rs = null;
			stmtObject = conn.getJDBCStatement();
			byte[] buffer = new byte[1024 * 64];
			String data = new String();

			boolean results = stmtObject.execute("SELECT * FROM " + conn.getDataLib() + ".ZWXDFNP WHERE DFNAME = '" + name.trim() + "'" );
			if (results) {
				rs = stmtObject.getResultSet();

				results = rs.first();
				if( results ) {
					Date lastUpdate = null;
					try {
						lastUpdate = rsdf.parse(new String(df8.format(rs.getLong("DFMDAT")) + " " + df6.format(rs.getLong("DFMTIM"))));
					} catch( Throwable t) {
						log.log(Level.WARNING, t.getMessage(), t);
					}

					WSDescriptor res = new WSDescriptor();
					WSDescriptor cached = getCachedService(name.trim());

					// Cache control
					if (cached == null || lastUpdate == null || cached.getUpdateDateTime() == null
							|| cached.getUpdateDateTime().before(lastUpdate)) {
						try {
							log.log(Level.INFO, "Recovering service definition from IFS....");
							IFSFileInputStream ifs = null;
							ifs = new IFSFileInputStream(conn.getConnection(), conn.getDefPath() + name.trim(), IFSFileInputStream.SHARE_NONE);

							// Read the first 64K bytes from the source file.
							int bytesRead = ifs.read(buffer);

							// While there is data in the source file copy the data from
							// the source file to the target file.

							while (bytesRead > 0) {
								data = data + new String(buffer, 0, bytesRead, conn.getCharset());
								bytesRead = ifs.read(buffer);
							}

							ifs.close();

							res = new Gson().fromJson(data, WSDescriptor.class);
							res.setName(rs.getString("DFNAME").trim());
							res.setDescription(rs.getString("DFDESC").trim());
							res.setType(rs.getString("DFTYPE").trim());
							try {
								res.setCreationDateTime(rsdf.parse(new String(df8.format(rs.getLong("DFCDAT")) + " " + df6.format(rs.getLong("DFCTIM")))));
							} catch( Throwable t ) {
								log.log(Level.WARNING, t.getMessage(), t);
							}
							try {
								res.setUpdateDateTime(rsdf.parse(new String(df8.format(rs.getLong("DFMDAT")) + " " + df6.format(rs.getLong("DFMTIM")))));
							} catch( Throwable t ) {
								log.log(Level.WARNING, t.getMessage(), t);
							}

							setCachedService(name.trim(), res);

						} catch( Exception e1 ) {
							throw DCMExceptionHelper.notFoundException(name);
						} finally {
							rs.close();
						}

						return res;
					} else {
						return cached;
					}

				} else {
					throw DCMExceptionHelper.notFoundException(name);
				}
			} else {
				throw DCMExceptionHelper.notFoundException(name);
			}

		} catch( DCMException e ) {
			throw e;
		} catch( Exception e ) {
			throw DCMExceptionHelper.defaultException(e.getMessage(), e);
		}
	}
	
	/**
	 * Executes a specific chain step command and returns its results
	 * 
	 * @param step
	 *            The step to execute
	 * @param parameters
	 *            a Map of parameters for that step
	 * @param stdout
	 *            The standard output
	 * @param stderr
	 *            The standard error
	 * @return the exit status of the process
	 */
	public int executeCurlOutgoingService(final WSDescriptor wsd, final Map<String, Object> parameters, final StringBuffer stdout, final StringBuffer stderr, final StringBuffer commandLine) {

		final StringBuffer exitStatus = new StringBuffer();

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {

				try {

					String[] cmd = getCommandLine(wsd, parameters);
					commandLine.setLength(0);
					for( int i = 0; i < cmd.length; i++) {
						if( i != 0 ) commandLine.append(" ");
						commandLine.append(cmd[i]);
					}
					
					log.log(Level.INFO, "Executing command line: " + commandLine.toString());
					
					ProcessBuilder processBuilder = new ProcessBuilder(cmd);
					Process process = processBuilder.start();

					stdout.setLength(0);
					stderr.setLength(0);

					byte[] tmp = new byte[1024];

					while(true){
						while(process.getInputStream().available()>0){
							int i=process.getInputStream().read(tmp, 0, 1024);
							if(i<0)break;
							stdout.append(new String(tmp, 0, i));
						}
						while(process.getErrorStream().available()>0){
							int i=process.getErrorStream().read(tmp, 0, 1024);
							if(i<0)break;
							stderr.append(new String(tmp, 0, i));
						}
						if(!process.isAlive()){
							exitStatus.append(String.valueOf(process.exitValue()));
							break;
						}
						try{Thread.sleep(1000);}catch(Exception ee){}
					}
					while(process.getInputStream().available()>0){
						int i=process.getInputStream().read(tmp, 0, 1024);
						if(i<0)break;
						stdout.append(new String(tmp, 0, i));
					}
					while(process.getErrorStream().available()>0){
						int i=process.getErrorStream().read(tmp, 0, 1024);
						if(i<0)break;
						stderr.append(new String(tmp, 0, i));
					}

				} catch( Exception e ) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		});

		t.start();
		try {
			t.join();
		} catch (Throwable e) {
		}

		try {
			return Integer.parseInt(exitStatus.toString());
		} catch( Throwable t1 ) {
			return -1;
		}
	}

	/**
	 * Executes a specific chain step command and returns its results
	 * 
	 * @param step
	 *            The step to execute
	 * @param parameters
	 *            a Map of parameters for that step
	 * @param stdout
	 *            The standard output
	 * @param stderr
	 *            The standard error
	 * @return the exit status of the process
	 */
	public int executeHttpOutgoingService(final WSDescriptor wsd, final Map<String, Object> parameters,
			final StringBuffer stdout, final StringBuffer stderr, final StringBuffer commandLine) throws DCMException {

		// Variable Definitions
		Iterator<String> i;
		HttpRequestBase request = null;
		String uri = wsd.getUrl();
		String method = wsd.getMethod();
		String body = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		final Map<String, Integer> respCode = CollectionFactory.createMap();

		// URI Replacement
		i = parameters.keySet().iterator();
		while(i.hasNext()) {
			String key = i.next();
			String val = parameters.get(key).toString();
			uri = replaceString("{{"+key+"}}", uri, val);
		}

		// Method parser
		if( method.equals("GET")) {
			request = new HttpGet(uri);
		} else if ( method.equals("POST")) {
			request = new HttpPost(uri);
		} else if ( method.equals("PUT")) {
			request = new HttpPut(uri);
		} else if ( method.equals("DELETE")) {
			request = new HttpDelete(uri);
		} else {
			throw DCMExceptionHelper.invalidArgumentsException("Invalid method " + method);
		}

		// Header Parser
		i = wsd.getHeaders().keySet().iterator();
		while( i.hasNext()) {
			String key = i.next();
			String val = wsd.getHeaders().get(key).toString();
			if( parameters.containsKey(key)) 
				val = parameters.get(key).toString();
			request.addHeader(key, val);
		}

		try {

			// Body parser
			if( method.equals("POST") || method.equals("PUT")) {
				body = wsd.getSendTemplate();
				i = parameters.keySet().iterator();
				while(i.hasNext()) {
					String key = i.next();
					String val = parameters.get(key).toString();
					body = replaceString("{{"+key+"}}", body, val);
				}
				HttpEntity reqEntity = new StringEntity(body);
				if( method.equals("POST"))
					((HttpPost)request).setEntity(reqEntity);
				else if( method.equals("PUT"))
					((HttpPut)request).setEntity(reqEntity);
			}

			// Create a custom response handler
			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

				@Override
				public String handleResponse(
						final HttpResponse response) throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					respCode.put("status", status);
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						return entity != null ? EntityUtils.toString(entity) : null;
					} else {
						throw new ClientProtocolException("Unexpected response status: " + status);
					}
				}

			};

			// Execute the service
			log.log(Level.INFO, "Executing request " + request.getRequestLine());
			String responseBody = httpclient.execute(request, responseHandler);
			stdout.append(responseBody);
			commandLine.append(request.getRequestLine());
			return respCode.get("status");

		} catch( Exception e ) {
		
			try {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				PrintStream writer = new PrintStream(os);
				e.printStackTrace(writer);
				os.flush();
				stderr.append(os.toString());
				os.close();
			} catch( Throwable t ) {}
			return respCode.containsKey("status") ? respCode.get("status") : -1;
		} finally {
			try {
				httpclient.close();
			} catch( Exception e1 ) {
				log.log(Level.WARNING, e1.getMessage(), e1);
			}
		}
	}

	/**
	 * Gets a step command line according to the sent parameters
	 * 
	 * @param step
	 *            The Chain Step to parse
	 * @param parameters
	 *            The set of named parameters to replace
	 * @return a full command line list
	 */
	public String[] getCommandLine(WSDescriptor wsd, Map<String, Object> parameters) {

		List<String> commands = CollectionFactory.createList();

		// Web Service Method
		commands.add("/usr/bin/curl");
		commands.add("-X");
		commands.add(wsd.getMethod());
		Iterator<String> i = wsd.getHeaders().keySet().iterator();
		while( i.hasNext()) {
			String key = i.next();
			String val = wsd.getHeaders().get(key).toString();
			if( parameters.containsKey(key)) 
				val = parameters.get(key).toString();
			commands.add("-H");
			commands.add(key + ": " + val);
		}
		commands.add("-d");
		String myParm = wsd.getSendTemplate();
		i = parameters.keySet().iterator();
		while(i.hasNext()) {
			String key = i.next();
			String val = parameters.get(key).toString();
			myParm = replaceString("{{"+key+"}}", myParm, val);
		}

		commands.add(myParm);


		String uri = wsd.getUrl();
		i = parameters.keySet().iterator();
		while(i.hasNext()) {
			String key = i.next();
			String val = parameters.get(key).toString();
			uri = replaceString("{{"+key+"}}", uri, val);
		}

		commands.add(uri);


		return commands.toArray(new String[commands.size()]);
	}

	/**
	 * Replaces a string
	 * 
	 * @param needle
	 *            The string to be replaced
	 * @param haystack
	 *            The word to be replaced
	 * @param replaceWord
	 *            The word to replace in the haystack
	 * @return The replaced string
	 */
	public String replaceString(String needle, String haystack, String replaceWord) {
		
		StringBuffer sb = new StringBuffer();
		int fromIndex = 0;
		int index = 0;

		index = haystack.indexOf(needle, fromIndex);
		while(index >= 0 ) {
			sb.append(haystack.substring(fromIndex, index));
			sb.append(replaceWord);
			fromIndex = index + needle.length();
			index = haystack.indexOf(needle, fromIndex);
		}
		sb.append(haystack.substring(fromIndex));
		return sb.toString();
	}

	/**
	 * Parses a response to a response record format
	 * 
	 * @param rd
	 *            The response record format
	 * @param inputString
	 *            The JSON String to parse
	 * @return A String mapping the response record format
	 * @throws DCMException
	 */
	public String parseResponse(RecordDescriptor rd, String inputString) throws DCMException {
		
		try {
			JSONObject json = new JSONObject(inputString);
			String response = RecordHelper.fromJson(rd, json);
			return response;
		} catch( Exception e ) {
			throw DCMExceptionHelper.defaultException(e.getMessage(), e);
		}
	}
}

package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;

import com.amazonaws.lambda.demo.model.Usuario;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class LambdaFunctionHandler implements RequestHandler<S3Event, String> {

	private static final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
			.withRegion(Regions.US_EAST_1).build();
	private static final AmazonDynamoDB client = AmazonDynamoDBClientBuilder
			.standard().withRegion(Regions.US_EAST_1).build();
	private static final String TABLE_NAME = "db-usuario-prueba";
	private static final DynamoDB dynamoDB = new DynamoDB(client);
	LambdaLogger log = null;

	public LambdaFunctionHandler() {
	}

	@Override
	public String handleRequest(S3Event event, Context context) {
		this.log = context.getLogger();
		log.log("Lambda Function Started");
		// Get the object from the event and show its content type
		try {
			S3EventNotificationRecord record = event.getRecords().get(0);
			String srcBucket = record.getS3().getBucket().getName();
			String srcKey = record.getS3().getObject().getKey();

			srcKey = URLDecoder.decode(srcKey, "UTF-8");

			S3Object response = s3.getObject(new GetObjectRequest(srcBucket, srcKey));


			if (response != null){
				switch(srcKey){
					case "usuario-prueba.csv":
						readCSVUsuario(response);
						log.log("Obtuvo el archivo");
						break; 
				}
				
			}

		} catch (Exception ex) {
			log.log("Entro en Exception " + ex.getMessage());
		}

		return "True";
	}

	private void readCSVUsuario(S3Object response)
			throws NumberFormatException, IOException {
		// TODO Auto-generated method stub

		BufferedReader br = new BufferedReader(new InputStreamReader(
				response.getObjectContent()));
		Usuario usuario = null;

		String csvOutput;
		int iteration = 0;
		while ((csvOutput = br.readLine()) != null) {
			String[] linea = csvOutput.split(";");
			if (iteration == 0) {
				iteration++;
				continue;
			}

			if (linea.length < 5) {
				this.log.log("\nDatos incompletos no se puede insertar");
			} else {
				usuario = new Usuario(
						Long.valueOf(linea[0].replace("\"\"", "")),
						Long.valueOf(linea[1].replace("\"", "")),
						linea[2].replace("\"", ""), linea[3].replace("\"", ""),
						linea[4].replace("\"", ""));
				insertItemUsuario(usuario);
			}

		}

	}

	private void insertItemUsuario(Usuario usuario) {
		// TODO Auto-generated method stub

		try {
			if (usuario.getId() == 0 || usuario.getEdad() == 0
					|| usuario.getIdentificacion().isEmpty()
					|| usuario.getNombreCompleto().isEmpty()
					|| usuario.getSexo().isEmpty()) {
				this.log.log("\nDatos incompletos no se puede insertar");
			} else {
				LambdaFunctionHandler.dynamoDB.getTable(TABLE_NAME).putItem(
						new PutItemSpec().withItem(new Item()
								.withLong("id", usuario.getId())
								.withLong("edad", usuario.getEdad())
								.withString("identificacion",
										usuario.getNombreCompleto())
								.withString("nombreCompleto",
										usuario.getNombreCompleto())
								.withString("sexo", usuario.getSexo())));
			}
		} catch (Exception e) {
			this.log.log("\nEnto en excepcion: ");
		}
	}
}
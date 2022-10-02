package com.amazonaws.lambda.demo.model;

public class Usuario {

	private long id; 
	private long edad; 
	private String identificacion; 
	private String nombreCompleto; 
	private String sexo;
	public Usuario(long id, long edad, String identificacion,
			String nombreCompleto, String sexo) {
		super();
		this.id = id;
		this.edad = edad;
		this.identificacion = identificacion;
		this.nombreCompleto = nombreCompleto;
		this.sexo = sexo;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getEdad() {
		return edad;
	}
	public void setEdad(long edad) {
		this.edad = edad;
	}
	public String getIdentificacion() {
		return identificacion;
	}
	public void setIdentificacion(String identificacion) {
		this.identificacion = identificacion;
	}
	public String getNombreCompleto() {
		return nombreCompleto;
	}
	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	} 
	
}

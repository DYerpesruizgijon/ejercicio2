package com.example.ejercicio2.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity //Así se definen las tablas
public class Planta {

    @Id//Así se define una clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Así se especifica que el campo es autoincrementable
    private Long id;

    private String nombre;

    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name= "tipo_id")
    private Tipo tipo;
    
    private Double altura;
    private String ubicacion;
    private String rareza;
    private String notasCampo;

    public  Planta() {
    } // constructor vacío para JPA

    public Planta(String nombre, Tipo tipo, Double altura, String ubicacion, String rareza, String notasCampo) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.altura = altura;
        this.ubicacion = ubicacion;
        this.rareza = rareza;
        this.notasCampo = notasCampo;
    }

    @Override
    public String toString() {
        return String.format(
                "Planta[id=%d, nombre='%s', tipo='%s', altura=%f, ubicacion='%s']",
                id, nombre, tipo, altura, ubicacion
        );
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }

    public Double getAltura() { return altura; }
    public void setAltura(Double altura) { this.altura = altura; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getRareza() { return rareza; }
    public void setRareza(String rareza) { this.rareza = rareza; }

    public String getNotasCampo() { return notasCampo; }
    public void setNotasCampo(String notasCampo) { this.notasCampo = notasCampo; }
}

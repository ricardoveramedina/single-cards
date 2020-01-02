package com.pueblolavanda.singlecards.domain;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
public class Product {


    @JsonProperty("ID")
    private int id;

    @JsonProperty("Tipo")
    private String tipo = "";

    @JsonProperty("SKU")
    private String sku = "";

    @JsonProperty("Nombre")
    private String nombre = "";

    @JsonProperty("Publicado")
    private String publicado = "";

    @JsonProperty("¿Está destacado?")
    private boolean estaDestacado;

    @JsonProperty("Visibilidad en el catálogo")
    private String visibilidadCatalogo = "";

    @JsonProperty("Descripción corta")
    private String descripcionCorta = "";

    @JsonProperty("Descripción")
    private String descripcion = "";

    @JsonProperty("Día en que empieza el precio rebajado")
    private String diaInicioPrecioRebajado = "";

    @JsonProperty("Día en que termina el precio rebajado")
    private String diaTerminoPrecioRebajado = "";

    @JsonProperty("Estado del impuesto")
    private String estadoImpuesto = "";

    @JsonProperty("Clase de impuesto")
    private String claseImpuesto = "";

    @JsonProperty("¿En inventario?")
    private boolean enInventario;

    @JsonProperty("Inventario")
    private int inventario;

    @JsonProperty("¿Permitir reservas de productos agotados?")
    private boolean permitirReservasProductosAgotados;

    @JsonProperty("¿Vendido individualmente?")
    private boolean esVendidoIndividualmente;

    @JsonProperty("Peso (kg)")
    private double peso;

    @JsonProperty("Longitud (cm)")
    private double longitud;

    @JsonProperty("Ancho (cm)")
    private double ancho;

    @JsonProperty("Altura (cm)")
    private double altura;

    @JsonProperty("¿Permitir valoraciones de clientes?")
    private boolean permitirValoraciones;

    @JsonProperty("Nota de compra")
    private String notaCompra = "";

    @JsonProperty("Precio rebajado")
    private String precioRebajado = "";

    @JsonProperty("Precio normal")
    private BigDecimal precioNormal;

    @JsonProperty("Categorías")
    private String categorias = "";

    @JsonProperty("Etiquetas")
    private String etiquetas = "";

    @JsonProperty("Clase de envío")
    private String claseEnvio = "";

    @JsonProperty("Imágenes")
    private String imagenes = "";

    @JsonProperty("Límite de descargas")
    private String limiteDescargas = "";

    @JsonProperty("Días de expiración de la descarga")
    private String diasExpiracionDescarga = "";

    @JsonProperty("Superior")
    private String superior = "";

    @JsonProperty("Productos agrupados")
    private String productosAgrupados = "";

    @JsonProperty("Ventas dirigidas")
    private String ventasDirigidas = "";

    @JsonProperty("Ventas Cruzadas")
    private String ventasCruzadas = "";

    @JsonProperty("URL Externa")
    private String urlExterna = "";

    @JsonProperty("Texto del botón")
    private String textoBoton = "";

    @JsonProperty("Posición")
    private int posicion;

    @JsonProperty("Nombre del atributo 1")
    private String nombreAtributo1 = "";

    @JsonProperty("Valor(es) del atributo 1")
    private String valorAtributo1 = "";

    @JsonProperty("Atributo visible 1")
    private String atributoVisible1 = "";

    @JsonProperty("Atributo global 1")
    private String atributoGlobal1 = "";

    @JsonProperty("Nombre del atributo 2")
    private String nombreAtributo2 = "";

    @JsonProperty("Valor(es) del atributo 2")
    private String valorAtributo2 = "";

    @JsonProperty("Atributo visible 2")
    private String atributoVisible2 = "";

    @JsonProperty("Atributo global 2")
    private String atributoGlobal2 = "";

    @JsonProperty("Nombre del atributo 3")
    private String nombreAtributo3 = "";

    @JsonProperty("Valor(es) del atributo 3")
    private String valorAtributo3 = "";

    @JsonProperty("Atributo visible 3")
    private String atributoVisible3 = "";

    @JsonProperty("Atributo global 3")
    private String atributoGlobal3 = "";

    @JsonProperty("Nombre del atributo 4")
    private String nombreAtributo4 = "";

    @JsonProperty("Valor(es) del atributo 4")
    private String valorAtributo4 = "";

    @JsonProperty("Atributo visible 4")
    private String atributoVisible4 = "";

    @JsonProperty("Atributo global 4")
    private String atributoGlobal4 = "";

    @JsonProperty("Nombre del atributo 5")
    private String nombreAtributo5 = "";

    @JsonProperty("Valor(es) del atributo 5")
    private String valorAtributo5 = "";

    @JsonProperty("Atributo visible 5")
    private String atributoVisible5 = "";

    @JsonProperty("Atributo global 5")
    private String atributoGlobal5 = "";

    @JsonProperty("Nombre del atributo 6")
    private String nombreAtributo6 = "";

    @JsonProperty("Valor(es) del atributo 6")
    private String valorAtributo6 = "";

    @JsonProperty("Atributo visible 6")
    private String atributoVisible6 = "";

    @JsonProperty("Atributo global 6")
    private String atributoGlobal6 = "";

}

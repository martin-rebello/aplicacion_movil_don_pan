package com.example.donpan.clases

data class DetalleItem(
    val nombreProducto: String,
    val cantidad: Int,
    val precioUnitario: Int,
    val subtotal: Int = cantidad * precioUnitario
)

data class PedidoBoleta(
    val idPedido: Long,
    val idUsuario: Int,
    val fecha: String,
    val total: Int,
    val items: List<DetalleItem>
)
package com.example.repository

import com.example.models.StoreOrder
import com.example.repository.WalletRepository
import com.example.repository.AdminSettingsRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class OrderRepository(private val firestore: FirebaseFirestore) {

    private val orders = mutableListOf<StoreOrder>()
    private val walletRepository = WalletRepository()
    private val adminSettingsRepository = AdminSettingsRepository()

    fun getLiveOrdersForRestaurant(restaurantId: String): Flow<List<StoreOrder>> = callbackFlow {
        val listenerRegistration = firestore.collection("orders")
            .whereEqualTo("restaurantId", restaurantId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val firestoreOrders = snapshot.documents.mapNotNull { it.toObject(StoreOrder::class.java) }
                    trySend(firestoreOrders).isSuccess
                }
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    suspend fun createOrder(order: StoreOrder) {
        val documentRef = if (order.id.isEmpty()) {
            firestore.collection("orders").document()
        } else {
            firestore.collection("orders").document(order.id)
        }
        val finalOrder = order.copy(id = documentRef.id)
        documentRef.set(finalOrder).await()
    }

    fun getOrders(): List<StoreOrder> {
        return orders
    }

    fun addOrder(order: StoreOrder) {
        orders.add(order)
    }

    suspend fun acceptOrder(orderId: String, driverId: String): Boolean {
        // 1. Get settings
        val settings = adminSettingsRepository.getSettings()
        // 2. Get wallet
        val wallet = walletRepository.getWallet(driverId) ?: return false
        
        // 3. Check balance
        if (wallet.balance < settings.minBalanceToAcceptOrders) {
            return false
        }
        
        // 4. Deduct fee
        val success = walletRepository.processTransaction(
            driverId, -settings.feePerDelivery, "Deduction", orderId, "Taxa por entrega"
        )
        
        // 5. Update order status
        if (success) {
            val order = orders.find { it.id == orderId }
            if (order != null) {
                order.status = "Aceito"
                return true
            }
        }
        return false
    }

    fun updateOrder(order: StoreOrder) {
        val index = orders.indexOfFirst { it.id == order.id }
        if (index != -1) {
            orders[index] = order
        }
    }

    companion object {
        val instance by lazy { OrderRepository(FirebaseFirestore.getInstance()) }
    }
}

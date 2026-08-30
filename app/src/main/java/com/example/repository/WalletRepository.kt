package com.example.repository

import com.example.models.DriverWallet
import com.example.models.WalletTransaction
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class WalletRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getAllWallets(): List<DriverWallet> {
        val snapshot = db.collection("driver_wallets").get().await()
        return snapshot.toObjects(DriverWallet::class.java)
    }

    suspend fun getAllTransactions(): List<WalletTransaction> {
        val snapshot = db.collection("wallet_transactions").get().await()
        return snapshot.toObjects(WalletTransaction::class.java)
    }

    suspend fun getWallet(driverId: String): DriverWallet? {
        val snapshot = db.collection("driver_wallets").document(driverId).get().await()
        return snapshot.toObject(DriverWallet::class.java)
    }

    suspend fun getTransactions(driverId: String): List<WalletTransaction> {
        val snapshot = db.collection("wallet_transactions")
            .whereEqualTo("driverId", driverId)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get().await()
        return snapshot.toObjects(WalletTransaction::class.java)
    }

    suspend fun processTransaction(
        driverId: String,
        amount: Double,
        type: String,
        orderId: String?,
        reason: String
    ): Boolean {
        return try {
            db.runTransaction { transaction ->
                val walletRef = db.collection("driver_wallets").document(driverId)
                val snapshot = transaction.get(walletRef)
                val wallet = snapshot.toObject(DriverWallet::class.java) ?: DriverWallet(driverId = driverId)
                
                val newBalance = wallet.balance + amount
                val updatedWallet = wallet.copy(
                    balance = newBalance,
                    totalRecharge = if (amount > 0) wallet.totalRecharge + amount else wallet.totalRecharge,
                    totalSpent = if (amount < 0) wallet.totalSpent - amount else wallet.totalSpent,
                    lastRecharge = if (amount > 0) System.currentTimeMillis() else wallet.lastRecharge
                )
                
                transaction.set(walletRef, updatedWallet)
                
                val transactionRef = db.collection("wallet_transactions").document()
                val walletTransaction = WalletTransaction(
                    id = transactionRef.id,
                    driverId = driverId,
                    amount = amount,
                    type = type,
                    timestamp = System.currentTimeMillis(),
                    orderId = orderId,
                    previousBalance = wallet.balance,
                    newBalance = newBalance,
                    reason = reason
                )
                transaction.set(transactionRef, walletTransaction)
            }.await()
            true
        } catch (e: Exception) {
            false
        }
    }

}

package edu.utap.catnapp.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

// Firebase demands an empty constructor, so all fields must be optional
data class CatPhoto(
    var rowID: String = "", // rowID is the primary key generated by firestore
    var userId: String? = null,
    var username: String? = null,
    var pictureURL: String? = null,
    var breed: String? = null,
    var description: String? = null,
    @ServerTimestamp val timeStamp: Timestamp? = null
)
package com.innopolis.innoqueue.webclients.firebase.service

import com.innopolis.innoqueue.webclients.firebase.model.FirebaseRecipients
import com.innopolis.innoqueue.webclients.firebase.model.TitleBody

interface NotificationTextFormerService {
    fun getTitleBody(firebaseRecipients: FirebaseRecipients, isPersonal: Boolean): TitleBody
}

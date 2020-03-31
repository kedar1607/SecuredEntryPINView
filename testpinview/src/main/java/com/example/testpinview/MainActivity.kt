package com.example.testpinview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dottedsecureentrypinview.CustomizablePINView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), CustomizablePINView.PINEntryCompleteListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pinview.pinEntryCompleteListener = this
    }

    override fun securedEntryCompleted() {
        super.securedEntryCompleted()
        pinview.showUserError()
    }
}

package com.example.dottedsecureentrypinview

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.example.dottedsecureentrypinview.extentions.changeVisibilityWithScaleAnimation
import kotlinx.android.synthetic.main.pin_view.view.*
import kotlinx.android.synthetic.main.pin_view_single_secured_digit.view.*


class CustomizablePINView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    interface PINEntryCompleteListener {
        fun securedEntryCompleted() {}
    }

    private var numberOfDigits: Int = DEFAULT_NUMBER_OF_DIGITS

    private var outerContainerSize = context.resources.getDimensionPixelOffset(R.dimen.pd200)

    private var outerContainerActivatedBorderColor =
        ContextCompat.getColor(context, R.color.colorAccent)

    private var outerContainerNotActivatedBorderColor =
        ContextCompat.getColor(context, R.color.mid_grey)

    private var outerContainerBorderWidthActivated =
        context.resources.getDimensionPixelOffset(R.dimen.pd1)

    private var outerContainerBorderWidthNotActivated =
        context.resources.getDimensionPixelOffset(R.dimen.pdHalf)

    private var outerContainerCornerRadius: Float = 0f

    private var outerContainerMargin = context.resources.getDimensionPixelOffset(R.dimen.pd50)

    private var outerContainerShape = GradientDrawable.RECTANGLE

    private var outerContainerBackgroundColor = ContextCompat.getColor(context, R.color.transparent)

    private var securedTextFillColor = ContextCompat.getColor(context, R.color.colorAccent)

    private var securedTextDiameter = context.resources.getDimensionPixelOffset(R.dimen.pd150)

    private var errorTextMessage = ""

    private var errorTextColor = ContextCompat.getColor(context, R.color.transparent)

    private val moveCursorToEnd = Runnable {
        tv_num.setSelection(tv_num.length())
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(editable: Editable?) {
            val length = editable?.length ?: 0

            pin_container.children.forEachIndexed { index, securedPINDigitView ->
                securedPINDigitView.fl_secured_text.changeVisibilityWithScaleAnimation(
                    index < length,
                    securedTextDiameter,
                    securedTextDiameter
                )
            }

            if (length == numberOfDigits) {
                pinEntryCompleteListener?.securedEntryCompleted()
            }
        }
    }

    var pinEntryCompleteListener: PINEntryCompleteListener? = null


    init {

        inflate(context, R.layout.pin_view, this)

        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CustomizablePINView)
        numberOfDigits = typedArray.getInt(
            R.styleable.CustomizablePINView_numberOfDigits,
            DEFAULT_NUMBER_OF_DIGITS
        )

        outerContainerSize = typedArray.getDimensionPixelOffset(
            R.styleable.CustomizablePINView_outerContainerSize,
            outerContainerSize
        )

        outerContainerActivatedBorderColor = typedArray.getColor(
            R.styleable.CustomizablePINView_outerContainerBorderColorActivated,
            outerContainerActivatedBorderColor
        )

        outerContainerNotActivatedBorderColor = typedArray.getColor(
            R.styleable.CustomizablePINView_outerContainerBorderColorNotActivated,
            outerContainerNotActivatedBorderColor
        )

        outerContainerBorderWidthActivated = typedArray.getDimensionPixelOffset(
            R.styleable.CustomizablePINView_outerContainerBorderWidthActivated,
            outerContainerBorderWidthActivated
        )

        outerContainerBorderWidthNotActivated = typedArray.getDimensionPixelOffset(
            R.styleable.CustomizablePINView_outerContainerBorderWidthNotActivated,
            outerContainerBorderWidthNotActivated
        )

        outerContainerMargin =
            typedArray.getDimensionPixelOffset(
                R.styleable.CustomizablePINView_outerContainerMargin,
                outerContainerMargin
            )

        outerContainerCornerRadius = typedArray.getDimension(
            R.styleable.CustomizablePINView_outerContainerCornerRadius,
            outerContainerCornerRadius
        )

        if (typedArray.hasValue(R.styleable.CustomizablePINView_outerContainerShape)) outerContainerShape =
            typedArray.getInt(
                R.styleable.CustomizablePINView_outerContainerShape,
                outerContainerShape
            )

        outerContainerBackgroundColor = typedArray.getColor(
            R.styleable.CustomizablePINView_outerContainerBackgroundColor,
            outerContainerBackgroundColor
        )


        securedTextDiameter =
            typedArray.getDimensionPixelOffset(
                R.styleable.CustomizablePINView_securedTextDiameter,
                securedTextDiameter
            )

        securedTextFillColor = typedArray.getColor(
            R.styleable.CustomizablePINView_securedTextFillColor,
            securedTextFillColor
        )

        errorTextMessage = typedArray.getString(
            R.styleable.CustomizablePINView_errorTextMessage
        ) ?: ""

        errorTextColor = typedArray.getColor(
            R.styleable.CustomizablePINView_errorTextColor,
            errorTextColor
        )

        setMaxLengthForEditText()

        assignErrorTextValues()

        tv_num.setOnClickListener { tv_num.postDelayed(moveCursorToEnd, 16L) }
        tv_num.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                tv_num.postDelayed(moveCursorToEnd, 16L)
                activateForPinEntry()
            } else {
                deActivateForPinEntry()
            }
        }

        tv_num.addTextChangedListener(textWatcher)

        for (index in 0 until numberOfDigits) {
            addSingleSecuredPINView(context, index)
        }

        typedArray.recycle()
    }

    private fun setMaxLengthForEditText() {
        val filterArray = arrayOfNulls<InputFilter>(1)
        filterArray[0] = InputFilter.LengthFilter(numberOfDigits)
        tv_num.filters = filterArray
    }

    private fun addSingleSecuredPINView(
        context: Context,
        index: Int
    ) {
        View.inflate(context, R.layout.pin_view_single_secured_digit, pin_container)
        setRootContainerMargins(index)

        val outerContainerBackgroundDrawable = GradientDrawable()
        setContainerShape(outerContainerBackgroundDrawable)
        setContainerStroke(outerContainerBackgroundDrawable)
        setContainerBackgroundColor(outerContainerBackgroundDrawable)
        setContainerLayoutParams(index)

        val securedTextBackgroundDrawable = GradientDrawable()
        securedTextBackgroundDrawable.shape = GradientDrawable.OVAL
        setSecuredTextLayoutParams(index)
        setSecuredTextFillColor(securedTextBackgroundDrawable)

        pin_container.getChildAt(index).fl_outer_container.background =
            outerContainerBackgroundDrawable
        pin_container.getChildAt(index).fl_secured_text.background = securedTextBackgroundDrawable
    }

    private fun setSecuredTextLayoutParams(index: Int) {
        val securedTextLayoutParams = FrameLayout.LayoutParams(
            securedTextDiameter,
            securedTextDiameter
        )

        securedTextLayoutParams.apply {
            gravity = Gravity.CENTER
        }

        pin_container.getChildAt(index).fl_secured_text.layoutParams = securedTextLayoutParams
    }

    private fun setSecuredTextFillColor(securedTextBackgroundDrawable: GradientDrawable) {
        securedTextBackgroundDrawable.setColor(
            securedTextFillColor
        )
    }

    private fun setContainerLayoutParams(index: Int) {

        val containerLayoutParams = FrameLayout.LayoutParams(
            outerContainerSize,
            outerContainerSize
        )

        pin_container.getChildAt(index).fl_outer_container.layoutParams = containerLayoutParams
    }

    private fun setRootContainerMargins(
        index: Int
    ) {

        val rootLayoutParams = LayoutParams(
            WRAP_CONTENT,
            WRAP_CONTENT
        )

        rootLayoutParams.apply {
            leftMargin = outerContainerMargin
            rightMargin = outerContainerMargin
            topMargin = outerContainerMargin
            bottomMargin = outerContainerMargin
            gravity = Gravity.CENTER
        }

        pin_container.getChildAt(index).fl_root.layoutParams = rootLayoutParams

    }

    private fun setContainerBackgroundColor(
        outerContainerBackgroundDrawable: GradientDrawable
    ) {
        outerContainerBackgroundDrawable.setColor(
            outerContainerBackgroundColor
        )
    }

    private fun setContainerStroke(
        outerContainerBackgroundDrawable: GradientDrawable
    ) {
        outerContainerBackgroundDrawable.setStroke(
            outerContainerBorderWidthNotActivated,
            outerContainerNotActivatedBorderColor
        )
    }

    private fun setContainerShape(
        outerContainerBackgroundDrawable: GradientDrawable
    ) {

        outerContainerBackgroundDrawable.shape =
            when (outerContainerShape) {
                0 -> {
                    outerContainerBackgroundDrawable.cornerRadius = outerContainerCornerRadius
                    GradientDrawable.RECTANGLE
                }
                else -> GradientDrawable.OVAL
            }

    }

    fun getValue() = tv_num.text.toString()

    fun clearValue() {
        tv_num.setText("")
    }

    fun startPINEntry() {
        tv_num.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.showSoftInput(tv_num, SHOW_IMPLICIT)
        activateForPinEntry()
    }

    fun showUserError() {
        clearValue()
        val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
        pin_container.startAnimation(shake)
        tv_user_error.visibility = View.VISIBLE
    }

    fun clearUserError() {
        tv_user_error.visibility = View.INVISIBLE
    }

    private fun assignErrorTextValues() {
        tv_user_error.text = errorTextMessage
        tv_user_error.setTextColor(errorTextColor)
    }

    private fun activateForPinEntry() {
        val outerContainerBackgroundDrawable = GradientDrawable()
        outerContainerBackgroundDrawable.setStroke(
            outerContainerBorderWidthActivated,
            outerContainerActivatedBorderColor
        )

        outerContainerBackgroundDrawable.cornerRadius = outerContainerCornerRadius
        pin_container.children.forEach {
            it.fl_outer_container.background = outerContainerBackgroundDrawable
        }
    }

    private fun deActivateForPinEntry() {
        val outerContainerBackgroundDrawable = GradientDrawable()
        outerContainerBackgroundDrawable.setStroke(
            outerContainerBorderWidthNotActivated,
            outerContainerNotActivatedBorderColor
        )

        outerContainerBackgroundDrawable.cornerRadius = outerContainerCornerRadius
        pin_container.children.forEach {
            it.fl_outer_container.background = outerContainerBackgroundDrawable
        }
    }

    companion object {
        const val DEFAULT_NUMBER_OF_DIGITS = 4
    }

}
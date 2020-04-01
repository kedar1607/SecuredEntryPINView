# SecuredEntryPINView

In order to use this PIN view use the following dependency in your app level build.gradle file - 

implementation 'com.github.kedar1607:SecuredEntryPINView:09b6b82045'

Following is the usage of the PIN view - 

<com.example.dottedsecureentrypinview.CustomizablePINView
        android:id="@+id/pinview"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:gravity="center"
        app:errorTextColor="@android:color/holo_red_dark"
        app:errorTextMessage="User Error"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:numberOfDigits="4"
        app:outerContainerBorderColorActivated="@color/colorAccent"
        app:outerContainerBorderColorNotActivated="@color/mid_grey"
        app:outerContainerCornerRadius="4dp"
        app:outerContainerShape="rectangle"
        app:securedTextDiameter="@dimen/pd50"
        app:securedTextFillColor="@android:color/black" />

Try out different styles by changing the app properties in above View.


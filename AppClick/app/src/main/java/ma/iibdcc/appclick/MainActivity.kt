package ma.iibdcc.appclick

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val clickButton: Button = findViewById(R.id.click)
        val longTextView: TextView = findViewById(R.id.longText)

        clickButton.setOnClickListener {
            Toast.makeText(this, "Button click", Toast.LENGTH_SHORT).show()
            longTextView.text = "Simple Click On Button"
        }

        clickButton.setOnLongClickListener {
            Toast.makeText(this@MainActivity, "Button Long click", Toast.LENGTH_SHORT).show()
            longTextView.text = "Long Click On Button"
            true
        }
    }
}
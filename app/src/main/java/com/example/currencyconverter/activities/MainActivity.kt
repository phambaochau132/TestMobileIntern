package com.example.currencyconverter.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.currencyconverter.R
import com.example.currencyconverter.databinding.ActivityMainBinding
import com.example.currencyconverter.models.Converter
import com.example.currencyconverter.models.CurrenciesApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : BaseActivity(), OnClickListener {
    private lateinit var binding: ActivityMainBinding

    companion object {
        var currency: String = ""
        var flag: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Khởi tạo gía trị đầu vào
        initialView()
        //Bắt sự kện cho button
        binding.btnCurrFrom.setOnClickListener(this)
        binding.btnCurrTo.setOnClickListener(this)
        binding.btnConvert.setOnClickListener(this)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun changeCurrency() {
        if (currency.isNotEmpty()) {
            if (flag == "from") {
                if (currency == binding.btnCurrTo.text.toString()) {
                    loadButtonCurrency(binding.btnCurrFrom, binding.btnCurrTo.text.toString())
                    loadButtonCurrency(binding.btnCurrTo, currency)
                } else {
                    loadButtonCurrency(binding.btnCurrFrom, currency)
                }


            } else {
                if (currency == binding.btnCurrFrom.text.toString()) {
                    loadButtonCurrency(binding.btnCurrTo, binding.btnCurrFrom.text.toString())
                    loadButtonCurrency(binding.btnCurrFrom, currency)
                } else {
                    loadButtonCurrency(binding.btnCurrTo, currency)

                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        changeCurrency()
    }

    private fun initialView() {
        //Load icon Flag vào button tiền tệ
        val urlFrom = "https://flagsapi.com/VN/flat/64.png"
        loadRoundedIcon(urlFrom, binding.btnCurrFrom)
        val urlTo = "https://flagsapi.com/US/flat/64.png"
        loadRoundedIcon(urlTo, binding.btnCurrTo)
        //Gắn nội dung cho button
        binding.btnCurrFrom.text = "VND";
        binding.btnCurrTo.text = "USD"
        //Lay ActionBar
        setSupportActionBar(binding.toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = "CONVERTER"
            actionBar.setDisplayShowHomeEnabled(true)
            actionBar.show()
        }
    }

    private fun loadButtonCurrency(button: TextView, curr: String) {
        val url = "https://flagsapi.com/${curr.substring(0, 2)}/flat/64.png"
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.btc)
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, false)
        val drawable = BitmapDrawable(resources, resizedBitmap)
        //Gắn nội dung cho button
        button.text = curr

        //Load icon Flag vào button tiền tệ
        if (curr != "BTC") {
            loadRoundedIcon(url, button)
        } else {
            button.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        }
    }

    private fun loadRoundedIcon(url: String, btnCurrency: TextView) {
        //Load hình ảnh từ Internet
        Glide.with(this).asBitmap().load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                    //Cắt hình ảnh nằm giữa khung hình
                    val bitm: Bitmap = cropCenterBitmap(bitmap, 74, 74)
                    //Bo tròn hình ảnh
                    val roundedIcon: RoundedBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(resources, bitm)
                    roundedIcon.cornerRadius = 100f
                    //Gắn hình ảnh vào phía bên phải của button
                    btnCurrency.setCompoundDrawablesWithIntrinsicBounds(
                        roundedIcon,
                        null,
                        null,
                        null
                    );
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }

            })
    }

    override fun onClick(v: View?) {
        //Chuyển hướng đến danh sách tiền tệ
        if (v!!.id == binding.btnCurrFrom.id) {
            val intent = Intent(this, CurrencyActivity::class.java)
            intent.putExtra("flag", "from")
            startActivity(intent)
        }
        if (v.id == binding.btnCurrTo.id) {
            val intent = Intent(this, CurrencyActivity::class.java)
            intent.putExtra("flag", "to")
            startActivity(intent)
        }
        //Thực hiện chuyển đổi tiền tệ
        if (v.id == binding.btnConvert.id) {
            val from: String
            val to: String
            val amount: Float
            if ((binding.edtNumFrom.text.isNotEmpty() && binding.edtNumFrom.isFocused) || (binding.edtNumTo.text.isNotEmpty() && binding.edtNumTo.isFocused)) {
                if (binding.edtNumFrom.isFocused) {
                    from = binding.btnCurrFrom.text.toString()
                    to = binding.btnCurrTo.text.toString()
                    amount = binding.edtNumFrom.text.toString().toFloatOrNull()!!

                } else {
                    from = binding.btnCurrTo.text.toString()
                    to = binding.btnCurrFrom.text.toString()
                    amount = binding.edtNumTo.text.toString().toFloatOrNull()!!

                }
                getResultFromWebService(from, to, amount)
            }
        }
    }

    private fun getResultFromWebService(from: String, to: String, amount: Float) {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(CurrenciesApi.BASE_URL_CONVERT)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        //Tao doi tuong CurrenciesApi
        val api = retrofit.create(CurrenciesApi::class.java)
        val call = api.convertCurrency(API_KEY_CONVERT)
        //Xu ly du lieu tra ve
        call.enqueue(object : Callback<Converter> {
            override fun onResponse(call: Call<Converter>, response: Response<Converter>) {
                if (response.isSuccessful) {
                    val convert: Converter? = response.body()
                    checkNotNull(convert)

                    convert.let {
                        val rateFrom = convert.rates[from]!!.toFloat()
                        val rateTo = convert.rates[to]!!.toFloat()
                        val rate: Float
                        val result: Float
                        if (binding.edtNumFrom.isFocused) {
                            if (rateFrom > rateTo) {
                                rate = (1 / rateTo) * rateFrom
                                binding.tvRate.text = "1$to = $rate$from"

                            } else {
                                rate = (1 / rateFrom) * rateTo
                                binding.tvRate.text = "1$from = $rate$to"
                            }
                            result = amount / rate
                            binding.edtNumTo.setText(result.toString())
                        } else {
                            if (rateFrom > rateTo) {
                                rate = (1 / rateTo) * rateFrom
                                binding.tvRate.text = "1$to = $rate$from"
                            } else {
                                rate = (1 / rateFrom) * rateTo
                                binding.tvRate.text = "1$from = $rate$to"
                            }
                            result = amount * rate
                            binding.edtNumFrom.setText(result.toString())
                        }
                    }
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Error: ${response.errorBody()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<Converter>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Load fail: ${t.message}", Toast.LENGTH_LONG)
                    .show()
            }

        })
    }
}
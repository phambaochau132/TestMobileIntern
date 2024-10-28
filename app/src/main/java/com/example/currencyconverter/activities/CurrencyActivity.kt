package com.example.currencyconverter.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.View.OnKeyListener
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.example.currencyconverter.R
import com.example.currencyconverter.adapters.RecyclerCurrencyAdapter
import com.example.currencyconverter.databinding.ActivityCurrencyBinding
import com.example.currencyconverter.models.Currencies
import com.example.currencyconverter.models.CurrenciesApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CurrencyActivity : BaseActivity(), OnClickListener, RecyclerCurrencyAdapter.OnItemListener {
    private lateinit var binding: ActivityCurrencyBinding
    private lateinit var currencies: Map<String, String>
    private lateinit var adapter: RecyclerCurrencyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrencyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Khởi tạo view đầu vào
        initialView()
        //Bắt sự kiện cho thanh tìm tiếm
        binding.edtSearch.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.btnHuy.visibility = VISIBLE
                } else {
                    binding.btnHuy.visibility = GONE
                }
            }
        binding.edtSearch.setOnEditorActionListener(object : OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                val temp = currencies
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    var result: Map<String, String> = mutableMapOf()
                    for (entry in currencies) {
                        val (key, value) = entry
                        if (key.contains(v!!.text, ignoreCase = true) || value.contains(
                                v.text,
                                ignoreCase = true
                            )
                        ) {
                            (result as MutableMap).put(key, value)
                        }
                    }
                    adapter = RecyclerCurrencyAdapter(
                        this@CurrencyActivity,
                        result,
                        this@CurrencyActivity
                    )
                    binding.cryCurrency.adapter = adapter

                    return true
                }
                return false
            }
        })
        binding.btnHuy.setOnClickListener(this)
    }

    private fun initialView() {
        //Lay ActionBar
        setSupportActionBar(binding.toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = "CURRENCY"
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.show()
        }
        currencies = mutableMapOf()
        //Đổ dữ liệu cho recyclerView
        getCurrenciesFromWebService()
    }

    private fun getCurrenciesFromWebService() {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(CurrenciesApi.BASE_URL_SYMBOLS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        //Tạo đối tượng CurrenciesApi
        val api = retrofit.create(CurrenciesApi::class.java)
        val call = api.getCurrencies(API_KEY_SYMBOLS)
        //Xu ly du lieu tra ve
        call.enqueue(object : Callback<Currencies> {
            override fun onResponse(call: Call<Currencies>, response: Response<Currencies>) {
                if (response.isSuccessful) {
                    val map = response.body()
                    map?.let {
                        //Đổ dữ liệu vào danh sách
                        currencies = map.symbols
                        //Truyền tham số cho adapter
                        adapter = RecyclerCurrencyAdapter(
                            this@CurrencyActivity,
                            currencies,
                            this@CurrencyActivity
                        )
                        binding.cryCurrency.adapter = adapter
                        //Set layout hiển thị cho recyclerView
                        binding.cryCurrency.layoutManager =
                            LinearLayoutManager(this@CurrencyActivity, VERTICAL, false)
                    }
                } else {
                    Toast.makeText(
                        this@CurrencyActivity,
                        "Error: ${response.errorBody()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<Currencies>, t: Throwable) {
                Toast.makeText(this@CurrencyActivity, "Error: ${t.message}", Toast.LENGTH_LONG)
                    .show()
            }

        })

    }

    override fun onClick(v: View?) {
        if (v!!.id == binding.btnHuy.id) {
            binding.edtSearch.text.clear()
            binding.edtSearch.clearFocus()
            v.visibility = GONE

            adapter = RecyclerCurrencyAdapter(this@CurrencyActivity, currencies, this)
            binding.cryCurrency.adapter = adapter
        }
    }

    override fun onClick(key: String) {
        MainActivity.currency = key
        MainActivity.flag = intent.getStringExtra("flag").toString()
        onBackPressed()
    }
}
package com.example.currencyconverter.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.currencyconverter.R
import com.example.currencyconverter.activities.CurrencyActivity
import com.example.currencyconverter.databinding.LayoutItemCurrencyBinding

class RecyclerCurrencyAdapter(
    private val context: Context,
    private val currencies: Map<String, String>,
    private val listener: OnItemListener
) : RecyclerView.Adapter<RecyclerCurrencyAdapter.MyHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutItemCurrencyBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val entry: Map.Entry<String, String> = currencies.entries.elementAt(position)
        holder.bind(entry, listener)
    }

    override fun getItemCount() = currencies.size

    class MyHolder(private val binding: LayoutItemCurrencyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(map: Map.Entry<String, String>, listener: OnItemListener) {
            val activity: CurrencyActivity = binding.root.context as CurrencyActivity
            binding.tvNameCurrency.text = map.value
            binding.tvKeyCurrency.text = map.key
            //Load image Flag
            if (map.key != "BTC") {
                val flag: String = map.key.substring(0, 2)
                val url = "https://flagsapi.com/$flag/flat/64.png"
                loadRoundedIcon(url, activity)
            } else {
                binding.imgFlag.setImageDrawable(activity.getDrawable(R.drawable.btc))
            }
            //Bat su kien click cho item
            binding.root.setOnClickListener { listener.onClick(map.key) }
        }

        private fun loadRoundedIcon(url: String, activity: CurrencyActivity) {
            Glide.with(activity).asBitmap().load(url)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        bitmap: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val bitm: Bitmap = activity.cropCenterBitmap(bitmap, 64, 64)
                        val roundedIcon: RoundedBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(activity.resources, bitm)
                        roundedIcon.cornerRadius = 100f
                        binding.imgFlag.setImageDrawable(roundedIcon)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }

                })
        }
    }

    interface OnItemListener {
        fun onClick(key: String)
    }
}

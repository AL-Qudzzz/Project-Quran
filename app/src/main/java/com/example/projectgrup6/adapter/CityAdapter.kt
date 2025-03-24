package com.example.projectgrup6

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectgrup6.databinding.ItemCityBinding

class CityAdapter(
    private val onCitySelected: (City) -> Unit
) : RecyclerView.Adapter<CityAdapter.ViewHolder>() {

    private var cities = listOf<City>()
    private var filteredCities = listOf<City>()
    private var selectedCity: City? = null

    class ViewHolder(
        private val binding: ItemCityBinding,
        private val onCitySelected: (City) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(city: City, isSelected: Boolean) {
            binding.cityName.text = city.name
            binding.root.isSelected = isSelected
            binding.root.setOnClickListener {
                onCitySelected(city)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onCitySelected)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val city = filteredCities[position]
        holder.bind(city, city == selectedCity)
    }

    override fun getItemCount(): Int = filteredCities.size

    fun updateCities(cities: List<City>) {
        this.cities = cities
        this.filteredCities = cities
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredCities = if (query.isEmpty()) {
            cities
        } else {
            cities.filter { it.name.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    fun setSelectedCity(city: City) {
        selectedCity = city
        notifyDataSetChanged()
    }

    fun getSelectedCity(): City? = selectedCity
}
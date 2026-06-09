<template>
  <div class="province-city-selector">
    <el-select
      v-model="selectedProvince"
      :placeholder="provincePlaceholder"
      :disabled="disabled"
      :clearable="!required"
      @change="handleProvinceChange"
      class="province-select"
    >
      <el-option
        v-for="item in provinceList"
        :key="item.code"
        :label="item.name"
        :value="item.name"
      ></el-option>
    </el-select>
    
    <el-select
      v-model="selectedCity"
      :placeholder="cityPlaceholder"
      :disabled="disabled || !selectedProvince"
      :clearable="!required"
      @change="handleCityChange"
      class="city-select"
    >
      <el-option
        v-for="item in cityList"
        :key="item.code"
        :label="item.name"
        :value="item.name"
      ></el-option>
    </el-select>
  </div>
</template>

<script>
import provinceCityData from '@/data/province-city.json'

export default {
  name: 'ProvinceCitySelector',
  props: {
    province: {
      type: String,
      default: ''
    },
    city: {
      type: String,
      default: ''
    },
    disabled: {
      type: Boolean,
      default: false
    },
    required: {
      type: Boolean,
      default: false
    },
    provincePlaceholder: {
      type: String,
      default: '请选择省份'
    },
    cityPlaceholder: {
      type: String,
      default: '请选择城市'
    }
  },
  data() {
    return {
      provinceList: provinceCityData,
      cityList: [],
      selectedProvince: this.province,
      selectedCity: this.city
    }
  },
  watch: {
    province(val) {
      this.selectedProvince = val
      this.updateCityList()
    },
    city(val) {
      this.selectedCity = val
    }
  },
  mounted() {
    this.updateCityList()
  },
  methods: {
    handleProvinceChange(val) {
      this.selectedCity = ''
      this.cityList = []
      this.$emit('update:province', val)
      this.$emit('update:city', '')
      this.$emit('province-change', val)
      this.$emit('change', { province: val, city: '' })
      
      if (val) {
        this.updateCityList()
      }
    },
    handleCityChange(val) {
      this.$emit('update:city', val)
      this.$emit('city-change', val)
      this.$emit('change', { province: this.selectedProvince, city: val })
    },
    updateCityList() {
      if (this.selectedProvince) {
        const province = this.provinceList.find(p => p.name === this.selectedProvince)
        if (province) {
          this.cityList = province.cities || []
        } else {
          this.cityList = []
        }
      } else {
        this.cityList = []
      }
    }
  }
}
</script>

<style scoped>
.province-city-selector {
  display: flex;
  gap: 10px;
  width: 100%;
}

.province-select {
  flex: 1;
}

.city-select {
  flex: 1;
}
</style>

<template>
  <img src="@/assets/images/paw.png" class="paw-image" :style="sizeStyle">
</template>

<script>
export default {
  name: 'PawIcon',
  props: {
    size: {
      type: [Number, String],
      default: 32
    }
  },
  computed: {
    sizeStyle() {
      const size = typeof this.size === 'number' ? `${this.size}px` : this.size
      return {
        width: size,
        height: size
      }
    }
  }
}
</script>

<style scoped>
.paw-image {
  object-fit: contain;
  display: inline-block;
  vertical-align: middle;
}
</style>

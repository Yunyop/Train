<template>
  <a-space direction="vertical">
    <a-input-search
        v-model:value="value"
        placeholder="input search text"
        style="width: 200px"
        @search="onSearch"
    />
    <a-input-search
        v-model:value="value"
        placeholder="input search text"
        enter-button
        @search="onSearch"
    />
    <a-input-search
        v-model:value="value"
        placeholder="input search text"
        enter-button="Search"
        size="large"
        @search="onSearch"
    />
    <a-input-search
        v-model:value="value"
        placeholder="input search text"
        size="large"
        @search="onSearch"
    >
      <template #enterButton>
        <a-button>Custom</a-button>
      </template>
    </a-input-search>
  </a-space>
</template>
<script setup>
import { ref } from 'vue';
const value = ref('');
const onSearch = searchValue => {
  console.log('use value', searchValue);
  console.log('or use this.value', value.value);
};
</script>
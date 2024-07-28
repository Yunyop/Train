<template>
  <a-select v-model:value="name" show-search allowClear
            :filter-option="filterNameOption"
            @change="onChange" placeholder="请选择车站"
            :style="'width:'+localWidth"
  >
    <a-select-option v-for="item in stations" :key="item.name" :value="item.name" :lable="item.namePinyin+item.start+item.namePy">
      {{item.name}}|{{item.namePinyin}}~{{item.namePy}}
    </a-select-option>
  </a-select>

</template>
<script>
import {onMounted, ref, watch,defineComponent} from "vue";
import {notification} from "ant-design-vue";
import axios from "axios";

export default defineComponent({
  name:"station-select-view",
  props:["modelValue","width"],
  emits:['update:modelValue','change'],
  setup(props, { emit }) {
    const name = ref();
    const stations = ref([]);
    const localWidth=ref(props.width);
    if (Tool.isEmpty(props.width)){
      localWidth.value="100%";
    }
    // 利用watch，动态获取父组件的值，如果在onMOunted或其他方法里，则只有第一次生效
    watch(()=>props.modelValue,()=>{
      console.log("props.modelValue",props.modelValue);
      name.value=props.modelValue;
    },{immediate:true});

    // 查询所有车站，用于车站下拉框
    const queryAllStation=()=>{
      axios.get("/business/admin/station/query-all").then((response)=>{
        let data=response.data;
        if (data.success){
          stations.value=data.content;
        }else {
          notification.error({description:data.message})
        }
      });
    };

    // 车站下拉框筛选
    const filterNameOption = (input,option) => {
      console.log(input,option);
      return option.lable.toLowerCase().indexOf(input.toLowerCase())>=0;
    };
    const onChange=(value)=>{
      emit("update:modelValue",value);
      let station=stations.value.filter(item=>item.code===value)[0];
      if (Tool.isEmpty(station)){
        station={};
      }
      emit('change',station);
    };
    onMounted(()=>{
      queryAllStation();
    });

    return{
      stations,
      name,
      filterNameOption,
      onChange,
      localWidth,
    }
  }
})

</script>



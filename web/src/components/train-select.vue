<template>
  <a-select v-model:value="trainCode" show-search allowClear
            :filter-option="filterTrainCodeOption"
            @change="onChange" placeholder="请选择车次"
            :style="'width:'+localWidth"
  >
    <a-select-option v-for="item in trains" :key="item.code" :value="item.code" :lable="item.code+item.start+item.end">
      {{item.code}}|{{item.start}}~{{item.end}}
    </a-select-option>
  </a-select>

</template>
<script>
import {onMounted, ref, watch,defineComponent} from "vue";
import {notification} from "ant-design-vue";
import axios from "axios";

export default defineComponent({
  name:"train-select-view",
  props:["modelValue","width"],
  emits:['update:modelValue','change'],
  setup(props, { emit }) {
    const trainCode = ref();
    const trains = ref([]);
    const localWidth=ref(props.width);
    if (Tool.isEmpty(props.width)){
      localWidth.value="100%";
    }
    // 利用watch，动态获取父组件的值，如果在onMOunted或其他方法里，则只有第一次生效
    watch(()=>props.modelValue,()=>{
      console.log("props.modelValue",props.modelValue);
      trainCode.value=props.modelValue;
    },{immediate:true});

    // 查询所有车次，用于车次下拉框
    const queryAllTrain=()=>{
      axios.get("/business/web/train/query-all").then((response)=>{
        let data=response.data;
        if (data.success){
          trains.value=data.content;
        }else {
          notification.error({description:data.message})
        }
      });
    };

    // 车次下拉框筛选
    const filterTrainCodeOption = (input,option) => {
      console.log(input,option);
      return option.lable.toLowerCase().indexOf(input.toLowerCase())>=0;
    };
    const onChange=(value)=>{
      emit("update:modelValue",value);
      let train=trains.value.filter(item=>item.code===value)[0];
      if (Tool.isEmpty(train)){
        train={};
      }
      emit('change',train);
    };
    onMounted(()=>{
      queryAllTrain();
    });

    return{
      trains,
      trainCode,
      filterTrainCodeOption,
      onChange,
      localWidth,
    }
  }
})

</script>



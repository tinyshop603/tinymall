<template>
  <div class="app-container calendar-list-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-search">测试打印</el-button>
      <el-button class="filter-item" type="primary" icon="el-icon-edit">打印预览</el-button>
      <el-button class="filter-item" type="primary" icon="el-icon-edit">保存配置</el-button>
    </div>
    <!-- 查询结果 -->
    <el-container style="height: 500px; border: 1px solid #eee">
       <el-aside width="30%" style="background-color: rgb(238, 241, 246)" >
          <el-menu>
            <el-menu-item :index="printer" v-for="(printer,i) in printers"><el-radio @change="selectedPrinterChangeCallback" v-model="selectedPrinter" :label="i" border>{{printer}}</el-radio></el-menu-item>
          </el-menu>
       </el-aside>
       <el-main>
        <el-table :data="printerDetails" height="250" @current-change="handleCurrentChange" highlight-current-row>
          <el-table-column prop="page" label="纸张规格" width="600">
          </el-table-column>
        </el-table>
      </el-main>
    </el-container>  
    <!-- 分页 -->
    <!-- 添加或修改对话框 -->
  </div>
</template>
<style>
.demo-table-expand {
  font-size: 0;
}

.demo-table-expand label {
  width: 200px;
  color: #99a9bf;
}

.demo-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
}

</style>
<script>
import { getLodop } from '@/api/printer'
import waves from '@/directive/waves' // 水波纹指令

export default {
  name: 'PrinterConfig',
  directives: {
    waves
  },
  data() {
    return {
      printerDetails:[],
      printers:[],
      selectedPrinter:0,
      currentRow:null
    }
  },
  created() {
  
  },
  mounted: function() {
    this.$nextTick(function() {
      // // 这样设计的原因是必须等文档挂接完毕之后才可调用,此处修改data数据
      const LODOP = getLodop()
      this.printers = this.getPrinterList(LODOP)
      let defaultPrinter = this.getDefaultPrinter(LODOP)
      this.printerDetails = this.getDefaultPrinterPageList(LODOP).map((item)=> {return {'page':item}}) 
      // 默认选中系统的默认打印机
      this.selectedPrinter = this.printers.findIndex((item)=> item===defaultPrinter)


      // debugger
    })
  },
  methods: {
  /**
   * 获取本机的打印设备信息,返回打印机的名称
   * @author zhaoguiyang 2018-11-05
   * @return {[type]} [description]
   */
    getPrinterList(LODOP) {
      if (!LODOP) {
        alert('没有打印机插件')
      }
      let printers = []
      var iPrinterCount = LODOP.GET_PRINTER_COUNT();
      for (var i = 0; i < iPrinterCount; i++) {
        printers.push(LODOP.GET_PRINTER_NAME(i));
      };
      return printers
    },
    getDefaultPrinter(LODOP){
      return LODOP.GET_PRINTER_NAME(-1)
    },
    getDefaultPrinterPageList(LODOP){
      return this.getPageListByPrinterNo(LODOP,-1)
    },
    getPageListByPrinterNo(LODOP,num){
      var strResult=LODOP.GET_PAGESIZES_LIST(num,'-');
      return strResult.split('-')
    },
    selectedPrinterChangeCallback(printer){
      this.printerDetails = this.getPageListByPrinterNo(LODOP,printer).map((item)=> {return {'page':item}})
    },
    handleCurrentChange(val) {
      this.currentRow = val;
    }
  }
}

</script>

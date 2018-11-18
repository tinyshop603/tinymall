<template>
  <div class="app-container calendar-list-container">
    <!-- 操作按钮 -->
    <div class="filter-container">
      <el-button class="filter-item" type="primary" @click="printerTest" v-waves icon="el-icon-printer">测试打印</el-button>
      <el-button class="filter-item" type="primary" @click="printerPreview" v-waves icon="el-icon-search">打印预览</el-button>
      <el-button class="filter-item" type="primary" @click="savePrinterConfig" v-waves icon="el-icon-edit">保存配置</el-button>
    </div>
    <!-- 打印机配置界面 -->
    <el-container style="height: 500px; border: 1px solid #eee">
       <el-aside width="30%" style="background-color: rgb(238, 241, 246)" >
          <el-menu>
            <el-menu-item :index="printer" v-for="(printer,i) in printers"><el-radio v-waves @change="selectedPrinterChangeCallback" v-model="selectedPrinter" :label="i" border>{{printer}}</el-radio></el-menu-item>
          </el-menu>
       </el-aside>
       <el-main>
        <el-table :data="printerDetails" height="450" @current-change="handleSelectedPageChange" highlight-current-row  ref="singleTable">
          <el-table-column v-waves prop="page" label="纸张规格" width="600">
          </el-table-column>
        </el-table>
      </el-main>
    </el-container>  
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
import {
  getLodop, getPrinterList, printerTestPrint,
  getDefaultPrinter, getDefaultPrinterPageList,
  getPageListByPrinterNo, printerPreview } from '@/api/printer'
import waves from '@/directive/waves' // 水波纹指令
import store from '@/store'

export default {
  name:'PrinterConfig',
  directives:{
    waves
  },
  data() {
    return {
      printerDetails:[],
      printers:[],
      selectedPrinter:0,
      selectedPage:null
    }
  },
  created() {
  },
  mounted:function() {
    this.$nextTick(function() {
      // // 这样设计的原因是必须等文档挂接完毕之后才可调用,此处修改data数据
      const LODOP = getLodop()
      this.printers = getPrinterList(LODOP)
      const defaultPrinter = getDefaultPrinter(LODOP)
      this.printerDetails = getDefaultPrinterPageList(LODOP).map((item) => { return { 'page':item } })
      // 默认选中系统的默认打印机
      this.selectedPrinter = this.printers.findIndex((item) => item === defaultPrinter)
      // 默认选中第一页纸
      //
      const that = this
      setTimeout(function() {
        that.$refs.singleTable.setCurrentRow(that.printerDetails[0])
      }, 0)
    })
  },
  methods:{
    selectedPrinterChangeCallback(printer) {
      this.printerDetails = getPageListByPrinterNo(LODOP, printer).map((item) => { return { 'page':item } })
      this.$refs.singleTable.setCurrentRow(this.printerDetails[0])
    },
    handleSelectedPageChange(val) {
      this.selectedPage = val
      // alert(val.page)
    },
    savePrinterConfig() {
      this.$store.dispatch('SavePrinterConfig', { 'index':this.selectedPrinter, 'pageName':this.selectedPage.page })
    },
    printerPreview() {
      printerPreview(getLodop(), this.selectedPrinter, this.selectedPage.page)
    },
    printerTest() {
      printerTestPrint(getLodop(), this.selectedPrinter, this.selectedPage.page)
      console.log(store.getters.printerIndex)
      console.log(store.getters.printerPageName)
    }
  }
}

</script>

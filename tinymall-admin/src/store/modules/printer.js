const printer = {
  state: {
    index: -1, // 打印机的索引,-1代表默认的打印机
    pageName: '' //打印机的纸张
  },

  mutations: {
    SET_INDEX: (state, index) => {
      state.index = index
    },
    SET_PAGE_NAME: (state, pageName) => {
      state.pageName = pageName
    }
  },

  actions: {
    SavePrinterConfig({ commit }, printer) {
      commit('SET_INDEX', printer.index)
      commit('SET_PAGE_NAME', printer.pageName)
    }
  }
}

export default printer

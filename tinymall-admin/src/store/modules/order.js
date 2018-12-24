const order = {
  state:{
    submitOrder:null,
    cancelOrder:null,
    refundOrder:null,
    confirmOrder:null
  },

  mutations:{
    SET_SUBMIT_ORDER:(state, obj) => {
      state.submitOrder = obj
    },
    SET_CANCEL_ORDER:(state, obj) => {
      state.cancelOrder = obj
    },
    SET_REFUND_ORDER:(state, obj) => {
      state.refundOrder = obj
    },
    SET_COMFIRM_ORDER:(state, obj) => {
      state.confirmOrder = obj
    }
  }
}

export default order

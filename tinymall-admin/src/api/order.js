import request from '@/utils/request'

export const STATUS = {
  CREATE: 101,
  PAY: 201,
  SHIP: 301,
  CONFIRM: 401,
  CANCEL: 102,
  AUTO_CANCEL: 103,
  REFUND: 202,
  REFUND_CONFIRM: 203,
  AUTO_CONFIRM: 402,
  RECEIVE_COMPLETE: 403
}
export function listOrder(query) {
  request.interceptors.response.use(response => {
    let orderItems = response.data.data.items
    if (!orderItems) {
      orderItems = [response.data.data]
    }
    response.data.data.items = orderItems.map((item) => {
      // 订单状态规则
      /**
       * 101,201,待发货
       * 301:已发货
       * 401:确认收货
       * 102,103,订单自动取消,或者由用户主动取消
       * 104,用户下单,店家未确认之前
       * 202:用户付款以后未发货前，点击退款按钮，系统进行设置退款状态，等待管理员退款操作
       * 203:管理员在管理后台看到用户的退款申请，则登录微信官方支付平台退款，然后回到 管理后台点击成功退款操作。
       * 402:用户已签收却不点击确认收货，超期7天以后，则系统自动确认收货。 用户不能再点击确认收货按钮，但是可以评价订单商品。
       * 403:订单全部完成状态
       */
      return Object.assign(item, getBtnStateByCode(item.orderStatus))
    })
    return response
  })
  return request({
    url: '/order/list',
    method: 'get',
    params: query
  })
}

export function createOrder(data) {
  return request({
    url: '/order/create',
    method: 'post',
    data
  })
}
export function readOrder(data) {
  return request({
    url: '/order/read',
    method: 'get',
    data
  })
}

export function updateOrder(data) {
  return request({
    url: '/order/update',
    method: 'post',
    data
  })
}
/**
 * [updateOrder 更新订单信息的状态吗]
 * @author zhaoguiyang 2018-07-01
 * @param  {[type]} data [description]
 * @return {[type]}      [description]
 */
export function updateOrderCode(data) {
  return request({
    url: '/order/update/status/',
    method: 'post',
    data
  })
}

export function deleteOrder(data) {
  return request({
    url: '/order/delete',
    method: 'post',
    data
  })
}
/**
 * 通过状态码获取按钮的状态
 * @author zhaoguiyang 2018-06-14
 * @param  {[type]} code [description]
 * @return {[type]}      [description]
 */
export function getBtnStateByCode(orderStaCode) {
  let code
  // 由于目前暂时为能够使用这么对状态码,故,将其进行简化
  if (orderStaCode === 101 || orderStaCode === 101) {
    code = 601
  } else if (orderStaCode === 102 || orderStaCode === 103) {
    code = 602
  } else if (orderStaCode === 104) {
    code = 603
  } else if (orderStaCode === 403) {
    code = 604
  }
  switch (code) {
    case 601: // 待发货的状态
      return getBtnsWithStatus(true, true, false)
    case 602: // 取消状态
      return getBtnsWithStatus(false, false, false)
    case 603: // 待确认状态
      return getBtnsWithStatus(false, true, true)
    case 604: // 订单全部完成
      return getBtnsWithStatus(false, false, false)
    default:
      return getBtnsWithStatus(true, true, true)
  }
}
/**
 * 根据状态值获取按钮的表现状态
 * [getBtnsWithStatus description]
 * @author zhaoguiyang 2018-06-14
 * @param  {[type]}  item            [description]
 * @param  {Boolean} isActiveSend    [是否激活发货按钮]
 * @param  {Boolean} isActiveCancel  [是否激活取消按钮]
 * @param  {Boolean} isActiveConfirm [是否激活确认按钮]
 * @return {[type]}                  [description]
 */
function getBtnsWithStatus(isActiveSend, isActiveCancel, isActiveConfirm) {
  return {
    sendBtnStatus: _getBtnByStates(isActiveSend),
    cancelBtnStatus: _getBtnByStates(isActiveCancel),
    confirmBtnStatus: _getBtnByStates(isActiveConfirm)
  }
}
/**
 * 通过是否激活按钮,快速创建状态按钮的样式
 * @author zhaoguiyang 2018-06-14
 * @param  {[type]} valid [是否为可用状态的按钮]
 * @return {[type]}       [description]
 */
function _getBtnByStates(valid) {
  return !valid ? {
    'type': 'info',
    'disabled': true
  } : {
    'type': 'primary',
    'disabled': false
  }
}

import request from '@/utils/request'

export function listOrder(query) {
  request.interceptors.response.use(response => {
    let list = response.data.data.items
    // 遍历list，添加按钮的状态
    list = list.map((item) => {
      item.sendBtnStatus = {
        'type': 'info',
        'disabled': true
      }
      item.cancelBtnStatus = {
        'type': 'primary',
        'disabled': false
      }
      item.confirmBtnStatus = {
        'type': 'success',
        'disabled': true
      }
      return item
    })
    return response;
  });
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

export function deleteOrder(data) {
  return request({
    url: '/order/delete',
    method: 'post',
    data
  })
}

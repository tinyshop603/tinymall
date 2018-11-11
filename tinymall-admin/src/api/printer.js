import QR_CODE from '@/api/base64Images'
var CreatedOKLodop7766 = null,
  CLodopIsLocal

//====判断是否需要 Web打印服务CLodop:===
//===(不支持插件的浏览器版本需要用它)===
function needCLodop() {
  try {
    var ua = navigator.userAgent;
    if (ua.match(/Windows\sPhone/i))
      return true;
    if (ua.match(/iPhone|iPod/i))
      return true;
    if (ua.match(/Android/i))
      return true;
    if (ua.match(/Edge\D?\d+/i))
      return true;

    var verTrident = ua.match(/Trident\D?\d+/i);
    var verIE = ua.match(/MSIE\D?\d+/i);
    var verOPR = ua.match(/OPR\D?\d+/i);
    var verFF = ua.match(/Firefox\D?\d+/i);
    var x64 = ua.match(/x64/i);
    if ((!verTrident) && (!verIE) && (x64))
      return true;
    else if (verFF) {
      verFF = verFF[0].match(/\d+/);
      if ((verFF[0] >= 41) || (x64))
        return true;
    } else if (verOPR) {
      verOPR = verOPR[0].match(/\d+/);
      if (verOPR[0] >= 32)
        return true;
    } else if ((!verTrident) && (!verIE)) {
      var verChrome = ua.match(/Chrome\D?\d+/i);
      if (verChrome) {
        verChrome = verChrome[0].match(/\d+/);
        if (verChrome[0] >= 41)
          return true;
      }
    }
    return false;
  } catch (err) {
    return true;
  }
}

//====页面引用CLodop云打印必须的JS文件,用双端口(8000和18000）避免其中某个被占用：====
if (needCLodop()) {
  var src1 = "http://localhost:8000/CLodopfuncs.js?priority=1";
  var src2 = "http://localhost:18000/CLodopfuncs.js?priority=0";

  var head = document.head || document.getElementsByTagName("head")[0] || document.documentElement;
  var oscript = document.createElement("script");
  oscript.src = src1;
  head.insertBefore(oscript, head.firstChild);
  oscript = document.createElement("script");
  oscript.src = src2;
  head.insertBefore(oscript, head.firstChild);
  CLodopIsLocal = !!((src1 + src2).match(/\/\/localho|\/\/127.0.0./i));
}

//====获取LODOP对象的主过程：====
export function getLodop(oOBJECT, oEMBED) {
  var strHtmInstall = "<br><font color='#FF00FF'>打印控件未安装!点击这里<a href='install_lodop32.exe' target='_self'>执行安装</a>,安装后请刷新页面或重新进入。</font>";
  var strHtmUpdate = "<br><font color='#FF00FF'>打印控件需要升级!点击这里<a href='install_lodop32.exe' target='_self'>执行升级</a>,升级后请重新进入。</font>";
  var strHtm64_Install = "<br><font color='#FF00FF'>打印控件未安装!点击这里<a href='install_lodop64.exe' target='_self'>执行安装</a>,安装后请刷新页面或重新进入。</font>";
  var strHtm64_Update = "<br><font color='#FF00FF'>打印控件需要升级!点击这里<a href='install_lodop64.exe' target='_self'>执行升级</a>,升级后请重新进入。</font>";
  var strHtmFireFox = "<br><br><font color='#FF00FF'>（注意：如曾安装过Lodop旧版附件npActiveXPLugin,请在【工具】->【附加组件】->【扩展】中先卸它）</font>";
  var strHtmChrome = "<br><br><font color='#FF00FF'>(如果此前正常，仅因浏览器升级或重安装而出问题，需重新执行以上安装）</font>";
  var strCLodopInstall_1 = "<br><font color='#FF00FF'>Web打印服务CLodop未安装启动，点击这里<a href='CLodop_Setup_for_Win32NT.exe' target='_self'>下载执行安装</a>";
  var strCLodopInstall_2 = "<br>（若此前已安装过，可<a href='CLodop.protocol:setup' target='_self'>点这里直接再次启动</a>）";
  var strCLodopInstall_3 = "，成功后请刷新本页面。</font>";
  var strCLodopUpdate = "<br><font color='#FF00FF'>Web打印服务CLodop需升级!点击这里<a href='CLodop_Setup_for_Win32NT.exe' target='_self'>执行升级</a>,升级后请刷新页面。</font>";
  var LODOP;
  try {
    var ua = navigator.userAgent;
    var isIE = !!(ua.match(/MSIE/i)) || !!(ua.match(/Trident/i));
    if (needCLodop()) {
      try {
        LODOP = getCLodop();
      } catch (err) {}
      if (!LODOP && document.readyState !== "complete") {
        alert("网页还没下载完毕，请稍等一下再操作.");
        return;
      }
      if (!LODOP) {
        document.body.innerHTML = strCLodopInstall_1 + (CLodopIsLocal ? strCLodopInstall_2 : "") + strCLodopInstall_3 + document.body.innerHTML;
        return;
      } else {
        if (CLODOP.CVERSION < "3.0.5.6") {
          document.body.innerHTML = strCLodopUpdate + document.body.innerHTML;
        }
        if (oEMBED && oEMBED.parentNode)
          oEMBED.parentNode.removeChild(oEMBED);
        if (oOBJECT && oOBJECT.parentNode)
          oOBJECT.parentNode.removeChild(oOBJECT);
      }
    } else {
      var is64IE = isIE && !!(ua.match(/x64/i));
      //=====如果页面有Lodop就直接使用，没有则新建:==========
      if (oOBJECT || oEMBED) {
        if (isIE)
          LODOP = oOBJECT;
        else
          LODOP = oEMBED;
      } else if (!CreatedOKLodop7766) {
        LODOP = document.createElement("object");
        LODOP.setAttribute("width", 0);
        LODOP.setAttribute("height", 0);
        LODOP.setAttribute("style", "position:absoluteleft:0pxtop:-100pxwidth:0pxheight:0px;");
        if (isIE)
          LODOP.setAttribute("classid", "clsid:2105C259-1E0C-4534-8141-A753534CB4CA");
        else
          LODOP.setAttribute("type", "application/x-print-lodop");
        document.documentElement.appendChild(LODOP);
        CreatedOKLodop7766 = LODOP;
      } else
        LODOP = CreatedOKLodop7766;
      //=====Lodop插件未安装时提示下载地址:==========
      if ((!LODOP) || (!LODOP.VERSION)) {
        if (ua.indexOf('Chrome') >= 0)
          document.body.innerHTML = strHtmChrome + document.body.innerHTML;
        if (ua.indexOf('Firefox') >= 0)
          document.body.innerHTML = strHtmFireFox + document.body.innerHTML;
        document.body.innerHTML = (is64IE ? strHtm64_Install : strHtmInstall) + document.body.innerHTML;
        return LODOP;
      }
    }
    if (LODOP.VERSION < "6.2.2.4") {
      if (!needCLodop())
        document.body.innerHTML = (is64IE ? strHtm64_Update : strHtmUpdate) + document.body.innerHTML;
      return LODOP;
    }
    //===如下空白位置适合调用统一功能(如注册语句、语言选择等):===

    //=======================================================
    return LODOP;
  } catch (err) {
    alert("getLodop出错:" + err);
  }
}

/**
 * 获取本机的打印设备信息,返回打印机的名称
 * @author zhaoguiyang 2018-11-05
 * @return {[type]} [description]
 */
export function getPrinterList(LODOP) {
  if (!LODOP) {
    alert('没有打印机插件')
  }
  let printers = []
  var iPrinterCount = LODOP.GET_PRINTER_COUNT();
  for (var i = 0; i < iPrinterCount; i++) {
    printers.push(LODOP.GET_PRINTER_NAME(i))
  };
  return printers
}
export function getDefaultPrinter(LODOP) {
  return LODOP.GET_PRINTER_NAME(-1)
}
export function getPageListByPrinterNo(LODOP, num) {
  var strResult = LODOP.GET_PAGESIZES_LIST(num, '-')
  return strResult.split('-')
}
export function getDefaultPrinterPageList(LODOP) {
  return getPageListByPrinterNo(LODOP, -1)
}

function fillPrinterData(LODOP, printData) {

  let topX = 0 // 基准X点坐标
  let topY = 0 // 基准Y点坐标 ,topY 后面的 加 的数值是距离上一个内容的距离
  let marginLeft = 0 // 距离左边边框的距离 

  const MAX_PAGE_WITH = 210 // 应当设置为打印机的最大宽度

  ///时间分割线
  const LINE_STYLE = 3; // 0--实线 1--破折线 2--点线 3--点划线 4--双点划线
  const TIME_TEXT_HEIGTH = 20;
  const LINE_HEIGTH = 1
  LODOP.ADD_PRINT_LINE(topY += 10, marginLeft, topY += 0, MAX_PAGE_WITH, LINE_STYLE, LINE_HEIGTH);
  LODOP.ADD_PRINT_TEXT(topY += 10, marginLeft, MAX_PAGE_WITH, TIME_TEXT_HEIGTH, new Date().toLocaleString('chinese', { hour12: false }));
  LODOP.ADD_PRINT_LINE(topY += TIME_TEXT_HEIGTH, marginLeft, topY += 0, MAX_PAGE_WITH, LINE_STYLE, LINE_HEIGTH);

  ///标题:商店名称
  const SHOP_TITLE_HEIGTH = 30
  LODOP.ADD_PRINT_TEXT(topY += 10, marginLeft += (MAX_PAGE_WITH / 3 - 15), MAX_PAGE_WITH, SHOP_TITLE_HEIGTH, printData.shopName);
  LODOP.SET_PRINT_STYLEA(0, "FontSize", 13); // 0代表当前样式
  LODOP.SET_PRINT_STYLEA(0, "Bold", 1);

  // 是否在线支付
  const ONLINE_PAY_HEIGTH = 30
  marginLeft = 0 // 重置左偏移量
  LODOP.ADD_PRINT_TEXT(topY += 30, marginLeft += 7, MAX_PAGE_WITH, ONLINE_PAY_HEIGTH, printData.payStyle);
  LODOP.SET_PRINT_STYLEA(0, "FontSize", 16); // 0代表当前样式
  LODOP.SET_PRINT_STYLEA(0, "Bold", 1);

  // 订单编号
  const ORDER_NO_HEIGTH = 10
  marginLeft = 0 // 重置左偏移量
  LODOP.ADD_PRINT_TEXT(topY += 30, marginLeft += 20, MAX_PAGE_WITH, ORDER_NO_HEIGTH, printData.orderNO);
  LODOP.SET_PRINT_STYLEA(0, "FontSize", 9); // 0代表当前样式

  // 下单时间
  const NEW_ORDER_HEIGTH = 10
  marginLeft = 0 // 重置左偏移量
  LODOP.ADD_PRINT_TEXT(topY += 20, marginLeft += 20, MAX_PAGE_WITH, NEW_ORDER_HEIGTH, printData.orderTime);
  LODOP.SET_PRINT_STYLEA(0, "FontSize", 9); // 0代表当前样式

  // 商品内容title文本
  marginLeft = 0
  LODOP.ADD_PRINT_LINE(topY += 25, marginLeft, topY += 0, MAX_PAGE_WITH / 3, LINE_STYLE, LINE_HEIGTH)
  LODOP.ADD_PRINT_TEXT(topY -= 5, marginLeft += (MAX_PAGE_WITH / 3 + 10), MAX_PAGE_WITH, 10, "商品");
  LODOP.ADD_PRINT_LINE(topY += 5, marginLeft += 30, topY += 0, MAX_PAGE_WITH, LINE_STYLE, LINE_HEIGTH)

  // 商品实际标题
  // 商品标题
  // let goodTitles = ['商品名称','商品数量','价格']
  // LODOP.ADD_PRINT_TEXT(150, 15, 50, 20, "商品编号");
  // LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
  // LODOP.SET_PRINT_STYLEA(0, "Bold", 1);
  // LODOP.ADD_PRINT_TEXT(150, 49, 50, 20, "商品名称");
  // LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
  // LODOP.SET_PRINT_STYLEA(0, "Bold", 1);
  // LODOP.ADD_PRINT_TEXT(150, 189, 50, 20, "商品数量");
  // LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
  // LODOP.SET_PRINT_STYLEA(0, "Bold", 1);
  // LODOP.ADD_PRINT_TEXT(150, 309, 50, 20, "单价(元)");
  // LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
  // LODOP.SET_PRINT_STYLEA(0, "Bold", 1);
  // LODOP.ADD_PRINT_LINE(172, 14, 73, 510, 0, 1);

  // 商品实际内容
  topY += 3
  for (let i = 0; i < printData.buyGoods.length; i++) {
    let good = printData.buyGoods[i]
    let sectionWith = MAX_PAGE_WITH / 4
    marginLeft = 0
    topY += 15
    LODOP.ADD_PRINT_TEXT(topY, marginLeft += 20, sectionWith + 15, 10, good.name);
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
    LODOP.ADD_PRINT_TEXT(topY, marginLeft += (sectionWith + 20), sectionWith, 10, `x${good.number}`);
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
    LODOP.ADD_PRINT_TEXT(topY, marginLeft += (sectionWith - 20), sectionWith + 15, 10, good.number * good.price);
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
  }

  // 其他内容title文本
  marginLeft = 0
  LODOP.ADD_PRINT_LINE(topY += 25, marginLeft, topY += 0, MAX_PAGE_WITH / 3, LINE_STYLE, LINE_HEIGTH)
  LODOP.ADD_PRINT_TEXT(topY -= 5, marginLeft += (MAX_PAGE_WITH / 3 + 10), MAX_PAGE_WITH, 10, "其他");
  LODOP.ADD_PRINT_LINE(topY += 5, marginLeft += 30, topY += 0, MAX_PAGE_WITH, LINE_STYLE, LINE_HEIGTH)

  // 其他内容的具体内容
  topY -= 5 // 调整间距
  for (let i = 0; i < printData.others.length; i++) {
    let other = printData.others[i]
    let sectionWith = MAX_PAGE_WITH
    marginLeft = 0
    topY += 15
    LODOP.ADD_PRINT_TEXT(topY, marginLeft += 20, sectionWith + 15, 10, `${other.name}: ${other.value}`);
    LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
  }


  // 以上所有内容的总结虚线
  marginLeft = 0
  LODOP.ADD_PRINT_LINE(topY += 20, marginLeft, topY += 0, MAX_PAGE_WITH, LINE_STYLE, LINE_HEIGTH);
  // 原价
  const ORIGN_PRICE_HEIGTH = 20
  marginLeft = 0 // 重置左偏移量
  LODOP.ADD_PRINT_TEXT(topY += 10, marginLeft += (MAX_PAGE_WITH / 5 * 2 + 20), MAX_PAGE_WITH, ORIGN_PRICE_HEIGTH, "原价: ¥" + printData.orignTotalPrice);
  LODOP.SET_PRINT_STYLEA(0, "FontSize", 10); // 0代表当前样式
  // 总价
  const TOTAL_PRICE_HEIGTH = 30
  marginLeft = 0 // 重置左偏移量
  LODOP.ADD_PRINT_TEXT(topY += 20, marginLeft += (MAX_PAGE_WITH / 5 * 2), MAX_PAGE_WITH, TOTAL_PRICE_HEIGTH, "总价: ¥" + printData.trueTotalPrice);
  LODOP.SET_PRINT_STYLEA(0, "FontSize", 13); // 0代表当前样式
  LODOP.SET_PRINT_STYLEA(0, "Bold", 1);

  // 地址
  marginLeft = 0 // 重置左偏移量
  LODOP.ADD_PRINT_TEXT(topY += 30, marginLeft, MAX_PAGE_WITH, 30, printData.customerInfo.address);
  LODOP.SET_PRINT_STYLEA(0, "FontSize", 13); // 0代表当前样式
  LODOP.SET_PRINT_STYLEA(0, "Bold", 1);
  // 电话
  LODOP.ADD_PRINT_TEXT(topY += (20 * Math.ceil((printData.customerInfo.address.length / 12))), marginLeft, MAX_PAGE_WITH, 30, printData.customerInfo.phoneNumber);
  LODOP.SET_PRINT_STYLEA(0, "FontSize", 13); // 0代表当前样式
  LODOP.SET_PRINT_STYLEA(0, "Bold", 1);
  // 姓名
  LODOP.ADD_PRINT_TEXT(topY += 20, marginLeft, MAX_PAGE_WITH, 30, printData.customerInfo.name);
  LODOP.SET_PRINT_STYLEA(0, "FontSize", 13); // 0代表当前样式
  LODOP.SET_PRINT_STYLEA(0, "Bold", 1);

  /// 添加二维码的图片
  marginLeft = 0
  const IMAGE_SIZE = 100
  LODOP.ADD_PRINT_IMAGE(topY += 20, marginLeft + (MAX_PAGE_WITH / 3), IMAGE_SIZE, IMAGE_SIZE, "<img border='0' src='" + printData.shopQRurl + "' />");
  LODOP.SET_PRINT_STYLEA(0, "Stretch", 2); //按原图比例(不变形)缩放模式

  // 添加关注我们
  marginLeft = 0
  LODOP.ADD_PRINT_TEXT(topY += (IMAGE_SIZE - 30), marginLeft += (MAX_PAGE_WITH / 3 + 10), MAX_PAGE_WITH, 10, "关注我们");
  LODOP.ADD_PRINT_TEXT(topY += 30, marginLeft, MAX_PAGE_WITH, 10, "");


  LODOP.SET_PRINT_PAGESIZE(3, MAX_PAGE_WITH * 3, 0, "");
}

function getTestData() {
  return {
    "shopName": "789便利店",
    "shopQRurl": "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif?imageView2/1/w/80/h/80",
    "payStyle": "在线支付(已支付)",
    "orderNO": "订单编号:20180270281272",
    "orderTime": "下单时间:2018/04/06 13:11:11",
    "buyGoods": [{
        "name": "名称1",
        "number": 1,
        "price": 10
      },
      {
        "name": "名称2",
        "number": 2,
        "price": 10
      }, {
        "name": "名称3",
        "number": 3,
        "price": 10
      }
    ],
    "others": [{
        "name": "配送费",
        "value": "0元"
      },
      {
        "name": "超时费",
        "value": "0元"
      }, {
        "name": "里程费",
        "value": "0元"
      }
    ],
    "orignTotalPrice": "32",
    "trueTotalPrice": "32",
    "customerInfo": {
      "name": "常波",
      "phoneNumber": "135XXXXXXXX",
      "address": "朝阳区驼房营西里11号楼404"
    }
  }
}

export function printerPreview(LODOP, printerIndex, pageName) {
  LODOP.PRINT_INIT("超市小票打印");
  LODOP.SET_PRINTER_INDEX(printerIndex);
  fillPrinterData(LODOP, getTestData());
  LODOP.PREVIEW();
}
export function printerTestPrint(LODOP, printerIndex, pageName) {
  LODOP.PRINT_INIT("超市小票打印");
  LODOP.SET_PRINTER_INDEX(printerIndex);
  fillPrinterData(LODOP, getTestData());
  LODOP.PRINT();
}

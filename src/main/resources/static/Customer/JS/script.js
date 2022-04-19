var currentPage = 1;
$(document).ready(function () {
    getTotalProductInCart();

    $("#modalCustomer").modal("show");

    $(".slider-for").slick({
        slidesToShow: 1,
        slidesToScroll: 1,
        arrows: false,
        fade: true,
        asNavFor: ".slider-nav",
        prevArrow: false,
        nextArrow: false,
    });
    $(".slider-nav").slick({
        slidesToShow: 3,
        slidesToScroll: 1,
        asNavFor: ".slider-for",
        dots: false,
        centerMode: true,
        focusOnSelect: true,
        prevArrow: false,
        nextArrow: false,
        responsive: [
            {
                breakpoint: 720,
                settings: {
                    slidesToShow: 3,
                    slidesToScroll: 1,
                },
            },
        ],
    });
    $(".autoplay").slick({
        slidesToShow: 6,
        slidesToScroll: 6,
        autoplay: true,
        autoplaySpeed: 4000,
        prevArrow: false,
        nextArrow: false,
        responsive: [
            {
                breakpoint: 720,
                settings: {
                    slidesToShow: 2,
                    slidesToScroll: 2,
                    autoplaySpeed: 4000,
                },
            },
        ],
    });

    //Đóng modal thông báo đặt hàng
    $("#modal-message").on("hidden.bs.modal", function () {
        window.location.href = "/cart";
    });

    //Đóng modal thông báo hủy đơn hàng
    $("#modal-cancel-order").on("hidden.bs.modal", function () {
        window.location.href = "/user/orders";
    });

});

//Hiển thị size tương ứng với màu sắc của sản phẩm
function getSizesOfProductByColor(color, productId) {
    $.ajax({
        type: "get",
        url: "/product/" + productId + "/variants",
        data: "color=" + encodeURIComponent(color),
        dataType: "json",
        success: function (response) {
            var currentSize = $('input[name="radioSize"]:checked').val();
            var element = document.getElementById("size");
            element.innerHTML = "";
            for (var i = 0; i < response.length; i++) {
                if(parseInt(response[i].quantity) > 0){
                    var data =
                        '<input type="radio" id="radioSize-' +
                        response[i].size +
                        '"' +
                        ' class="mx-5" name="radioSize"' +
                        'value="' +
                        response[i].size +
                        '">' +
                        '<label for="radioSize-' +
                        response[i].size +
                        '" class="mr-1 mt-0 mb-1 radioSize"' +
                        ">" +
                        response[i].size +
                        "</label>";
                }
                else{
                    var data =
                        '<input type="radio" id="radioSize-' +
                        response[i].size +
                        '"' +
                        ' class="mx-5" name="radioSize"' +
                        'value="' +
                        response[i].size +
                        '" disabled>' +
                        '<label for="radioSize-' +
                        response[i].size +
                        '" class="mr-1 mt-0 mb-1 radioSize"' +
                        " data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Hết hàng\" data-delay='{\"show\":0, \"hide\":3}'>" +
                        response[i].size +
                        "</label>";
                }
                element.innerHTML += data;
            }
            var input = document.getElementById("size").children;
            for (i = 0; i < input.length; i++) {
                if (input[i].value === currentSize || input[i].value === "Free size") {
                    input[i].checked = true;
                    break;
                }
            }
        },
        error: function (jqXHR, exception, errorThrown) {
            if (jqXHR.status === 404 || errorThrown == "Not Found") {
                window.location.href = "/";
            }
            if (jqXHR.status === 400 || errorThrown == "Bad Request") {
                window.location.href = "/";
            }
        },
    });
}

function quantityPlus(inputQuantity) {
    var quantity = parseInt(inputQuantity.val());
    if (Number.isNaN(quantity)) {
        quantity = 0;
    }
    inputQuantity.val(quantity + 1);
}

function quantityMinus(inputQuantity) {
    var quantity = parseInt(inputQuantity.val());
    if (Number.isNaN(quantity)) {
        inputQuantity.val(1);
    }
    if (quantity > 1) {
        inputQuantity.val(quantity - 1);
    }
}

function isInputNumber(evt) {
    var ch = String.fromCharCode(evt.which);
    if (!/[0-9]/.test(ch)) {
        evt.preventDefault();
    }
}

//Xem thêm sản phẩm ở trang chủ
function loadMoreProduct() {
    $.ajax({
        url: "/products",
        type: "get",
        data: {
            page: currentPage + 1,
        },
        success: function (response) {
            for (var i = 0; i < response.length; i++) {
                var data = '<div class="product col-md-4 col-6 p-1 p-md-3">' + '<div class="card">';
                if (response[i].productSale > 0) {
                    data += "<div class='ribbon-wrapper'>" + "<div class='ribbon'>Giảm " + response[i].productSale + "%</div>" + "</div>";
                }
                data +=
                    '<a href="/product/' +
                    response[i].productId +
                    "/" +
                    response[i].productName +
                    '"><img class="card-img-top p-md-3" src="/img/' +
                    response[i].image +
                    '" alt="image product" /></a>' +
                    '<div class="card-body">' +
                    '<p class="card-title text-center text-truncate"><a href="/product/' +
                    response[i].productId +
                    "/" +
                    response[i].productName +
                    '" class="text-decoration-none">' +
                    response[i].productName +
                    "</a></p>\n" +
                    '<h5 class="card-text font-weight-bold text-center">' +
                    response[i].productPrice.replace(/\B(?=(\d{3})+(?!\d))/g, ",") +
                    " đ</h5>\n" +
                    "</div>\n" +
                    "</div>\n" +
                    "</div>";
                var row = document.getElementById("list-product");
                row.innerHTML += data;
            }
            currentPage += 1;
        },
        error: function (jqXHR, exception, errorThrown) {
            if (jqXHR.status === 500) {
                window.location.href = "/";
            }
        },
    });
}

function addProductToCart() {
    var color = $('input[name="radioColor"]:checked').val();
    var size = $('input[name="radioSize"]:checked').val();
    var productId = $("#productId").val();
    var quantity = $("#quantity").val();
    $("#errorColor").text("");
    $("#errorSize").text("");
    $("#errorQuantity").text("");
    if(color === undefined || color === null || color.trim().length === 0){
        $("#errorColor").text("Vui lòng chọn màu sản phẩm.");
        return;
    }
    if(size === undefined || size === null || size.trim().length === 0){
        $("#errorSize").text("Vui lòng chọn kích cỡ sản phẩm.");
        return;
    }
    if(quantity === undefined || quantity === null || quantity.trim().length === 0){
        $("#errorQuantity").text("Vui lòng nhập số lượng");
        return;
    }
    if(quantity < 1){
        $("#errorQuantity").text("Số lượng không hợp lệ");
        return;
    }
    $.ajax({
        type: "post",
        url: "/cart/item",
        contentType: "application/json",
        dataType: "text",
        data: JSON.stringify({
          variantForm:{
            color: color,
            size: size,
            productForm:{
              productId: productId
            }
          },
          quantity: quantity
        }),
        success: function () {
            $("#modal-content").text("Thêm sản phẩm vào giỏ hàng thành công");
            $('#modalCart').modal('show');
            getTotalProductInCart();
        },
        error: function (jqXHR, exception, errorThrown) {
            if (jqXHR.status === 400 || errorThrown == "Bad Request") {
                var errors = JSON.parse(jqXHR.responseText);
                for(var i = 0; i < errors.length; i++){
                  if(errors[i].field === "variantForm.color"){
                      $("#errorColor").text(errors[i].defaultMessage);
                  }
                  else if(errors[i].field === "variantForm.size"){
                      $("#errorSize").text(errors[i].defaultMessage);
                  }
                  else if(errors[i].field === "quantity"){
                      $("#errorQuantity").text(errors[i].defaultMessage);
                  }
                }
            }
        },
        async: false
    });
}

function changeQuantity(inputQuantity, id) {
    var quantity = inputQuantity.val();
    if(quantity === null || quantity === undefined || quantity === ""){
        window.location.href = "/cart";
        return;
    }
    $.ajax({
        type: "put",
        url: "/cart/item/" + id,
        contentType: "application/json; charset=utf-8",
        dataType: "text",
        data: JSON.stringify({
            quantity: quantity
        }),
        success: function (response) {
            window.location.href = "/cart";
        },
        error: function (jqXHR, exception, errorThrown) {
            if (jqXHR.status === 400 || errorThrown == "Bad Request") {
                var errors = JSON.parse(jqXHR.responseText);
                for(var i = 0; i < errors.length; i++){
                  if(errors[i].field === "quantity"){
                      $("#error-item-" + id).text(errors[i].defaultMessage);
                  }
                }
            }
            if (jqXHR.status === 404 || errorThrown == "Not Found") {
                window.location.href = "/cart";
            }
        },
    });
}

//Đặt hàng
function order() {
    var addressOrder = $("#addressOrder").val();
    var numberPhoneOrder = $("#numberPhoneOrder").val();
    var paymentMethod = $("input[name='paymentMethod']:checked").val();
    $("#errorAddress").text("");
    $("#errorNumberPhone").text("");
    $("#errorPayment").text("");
    $(".error-item").text("");
    $.ajax({
        type: "post",
        url: "/order",
        contentType: "application/json; charset=utf-8",
        dataType: "text",
        data: JSON.stringify({
            orderAddress: addressOrder,
            orderNumberPhone: numberPhoneOrder,
            paymentsMethod: paymentMethod
        }),
        success: function (response) {
            $("#modal-message").modal('show');
            $("#modal-message-body").text("Đặt hàng thành công. Vui lòng xem lại thông tin đơn hàng");
            sendEmailConfirmOrder();
        },
        error: function (jqXHR, exception, errorThrown) {
            if (jqXHR.status === 403) {
                window.location.href = "/login";
            }
            if (jqXHR.status === 404 || errorThrown == "Not Found") {
                window.location.href = "/cart";
            }
            if (jqXHR.status === 400 || errorThrown == "Bad Request") {

                var errors = JSON.parse(jqXHR.responseText);
                for(var i = 0; i < errors.length; i++){
                    if(errors[i].field === "orderNumberPhone"){
                        $("#errorNumberPhone").text(errors[i].defaultMessage);
                    }
                    else if(errors[i].field === "orderAddress"){
                        $("#errorAddress").text(errors[i].defaultMessage);
                    }
                    else if(errors[i].field === "paymentsMethod"){
                        $("#errorPayment").text(errors[i].defaultMessage);
                    }
                    else if(errors[i].field === "orderDetailForms"){
                        $("#error-item-" + errors[i].code).text(errors[i].defaultMessage);
                    }
                }
            }
        },
    });
}

function sendEmailConfirmOrder(){
    console.log("sending email");
    $.ajax({
        type: "post",
        url: "/user/order/email",
        contentType: "application/json; charset=utf-8",
        dataType: "text",
        success: function (response) {
          console.log("sended email");
        },
        error: function (jqXHR, exception, errorThrown) {
//            if (jqXHR.status === 400 || errorThrown == "Bad Request") {
//                var errors = JSON.parse(jqXHR.responseText);
//                for(var i = 0; i < errors.length; i++){
//                  if(errors[i].field === "quantity"){
//                      $("#error-item-" + id).text(errors[i].defaultMessage);
//                  }
//                }
//            }
//            if (jqXHR.status === 404 || errorThrown == "Not Found") {
//                window.location.href = "/cart";
//            }
        },
    });
}

//Thay đổi địa chỉ nhận hàng
function changeAddressOrder() {
    var address = $("#addressOrderModal").val();
    if (address.trim() === "") {
        $("#errorAddressModal").text("Vui lòng nhập địa chỉ");
    } else if (address.trim().length > 100) {
        $("#errorAddressModal").text("Địa chỉ dài quá giới hạn(100 ký tự)");
    } else {
        $("#addressOrder").val(address);
        $("#errorAddressModal").text("");
        $("#modalAddress").modal("hide");
    }
}

//Thay đổi số điện thoại nhận hàng
function changeNumberPhone() {
    var numberPhone = $("#numberPhoneOrderModal").val();
    var numberPhonePattern1 = /^[0]{1}[0-9]{9}$/;
    var numberPhonePattern2 = /^[+]{1}[8]{1}[4]{1}[0-9]{9}$/;
    if (numberPhone.trim() === "") {
        $("#errorNumberPhoneModal").text("Vui lòng nhập số điện thoại");
    } else if (!numberPhonePattern1.test(numberPhone) && !numberPhonePattern2.test(numberPhone)) {
        $("#errorNumberPhoneModal").text("Số điện thoại không hợp lệ");
    } else {
        $("#numberPhoneOrder").val(numberPhone);
        $("#errorNumberPhoneModal").text("");
        $("#modalNumberPhone").modal("hide");
    }
}

function cancelOrder(orderId) {
    $.ajax({
        type: "put",
        url: "/user/order/" + orderId,
        success: function (response) {
            $("#modal-content").text(response);
            $("#modal-cancel-order").modal("show");
        },
        error: function (jqXHR, exception, errorThrown) {
            if (jqXHR.status === 403) {
                window.location.href = "/login";
            }
            if (jqXHR.status === 404 || errorThrown == "Not Found") {
                window.location.href = "/user/orders";
            }
            if (jqXHR.status === 400 || errorThrown == "Bad Request") {
                window.location.href = "/user/orders";
            }
        },
    });
}

function productFilter(maxPrice, minPrice) {
  var url = new URL("http://localhost:8080" + window.location.pathname);
  var key = new URLSearchParams(window.location.search).get("key");
  if(key != null){
    url.searchParams.append('key', key);
  }
  if(minPrice == null && maxPrice == null){
      window.location.href = url;
  }
  else{
      url.searchParams.append('minPrice', minPrice);
      url.searchParams.append('maxPrice', maxPrice);
      window.location.href = url;
  }
}

function getTotalProductInCart(){
    $.ajax({
        type: "get",
        url: "/cart/products/total",
        contentType: "application/json; charset=utf-8",
        dataType: "text",
        success: function (response) {
            $("#totalProductInCart").text("(" + response + ")");
            if(response > 0){
                $("#totalProductInCart").addClass("link-active");
            }
            else{
                $("#totalProductInCart").addClass("text-white");
            }
        },
    });
}

function deleteItemInCart(id){
  $.ajax({
      type: "delete",
      url: "/cart/item/" + id,
      success: function (response) {
          window.location.href = "/cart";
      },
      error: function (jqXHR, exception, errorThrown) {
          window.location.href = "/cart";
      }
  });
}

function totalCart(){
  $.ajax({
      type: "get",
      url: "/cart/total",
      success: function (response) {
          $("#totalProduct").text(response.totalProduct + " sản phẩm");
          $("#totalPrice").text(String(response.totalPrice).replace(/\B(?=(\d{3})+(?!\d))/g, ",") + " đ");
          $("#totalDiscount").text(String(response.totalDiscount).replace(/\B(?=(\d{3})+(?!\d))/g, ",") + " đ");
          $("#amount").text(String(response.totalPrice - response.totalDiscount).replace(/\B(?=(\d{3})+(?!\d))/g, ",") + " đ");
      },
      error: function (jqXHR, exception, errorThrown) {
          window.location.href = "/cart";
      }
  });
}

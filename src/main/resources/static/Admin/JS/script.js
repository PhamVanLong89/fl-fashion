$(document).ready(function () {
//  show modal add admin
  $('#modalAddAdmin').modal('show')

// show modal sản phẩm
  $("#modalProduct").modal("show");

    $(".option-variant").hide();
    $(".sidebar-dropdown > a").click(function () {
        $(".sidebar-submenu").slideUp(200);
        if ($(this).parent().hasClass("active")) {
            $(".sidebar-dropdown").removeClass("active");
            $(this).parent().removeClass("active");
        } else {
            $(".sidebar-dropdown").removeClass("active");
            $(this).next(".sidebar-submenu").slideDown(200);
            $(this).parent().addClass("active");
        }
    });

    $("#close-sidebar").click(function () {
        $(".page-wrapper").removeClass("toggled");
    });

    $("#show-sidebar").click(function () {
        $(".page-wrapper").addClass("toggled");
    });

    //Đóng modal sửa trạng thái đơn hàng
    $("#modal").on("hidden.bs.modal", function () {
        window.location.href = "/manager/orders";
    });

    //Đóng modal sửa trạng thái tài khoản khách hàng
    $('#modalChangeStatusCustomer').on('hidden.bs.modal', function () {
        window.location.href = "/manager/customers";
    });

    //Đóng modal thông báo xóa sản phẩm
    $("#modalMessageDeleteProduct").on("hidden.bs.modal", function () {
        window.location.href = "/manager/products";
    });

    //Đóng modal thông báo xóa biến thể sản phẩm -> get table variant
    $("#modalMessageDeleteVariant").on("hidden.bs.modal", function () {
        reloadTableVariant();
    });

    $('#filterProduct').collapse()

});


function checkVariant(color, size, SKU, quantity, productId) {
    var table = document.getElementById("tableProductVariant");
    var error = null;
    if (color === "") {
        error = "Vui lòng chọn màu sắc sản phẩm";
    } else if (size === "") {
        error = "Vui lòng chọn kích cỡ sản phẩm";
    } else if (SKU === "") {
        error = "Vui lòng nhập SKU sản phẩm";
    } else if (quantity === "") {
        error = "Vui lòng nhập số lượng sản phẩm";
    } else {
        for (var i = 1; i < table.rows.length; i++) {
            if ($("#productSKU").val().trim() === table.rows[i].cells[3].innerHTML) {
                error = "Mã SKU đã tồn tại";
                break;
            } else if ($("#productColor").val() === table.rows[i].cells[0].innerHTML) {
                if ($("#productSize").val() === table.rows[i].cells[1].innerHTML) {
                    error = "Sản phẩm màu: " + $("#productColor").val() + " cỡ: " + $("#productSize").val() + " đã được chọn.";
                    break;
                } else if ($("#productSize").val() !== "Free size" && table.rows[i].cells[1].innerHTML === "Free size") {
                    error = "Vui lòng xóa sản phẩm màu: " + $("#productColor").val() + " cỡ: Free size trước khi thêm kích cỡ khác.";
                    break;
                } else if ($("#productSize").val() === "Free size" && table.rows[i].cells[1].innerHTML !== "Free size") {
                    error = "Sản phẩm màu: " + $("#productColor").val() + " đã được chọn kích cỡ. Vui lòng chọn kích cỡ khác cho màu này.";
                    break;
                }
            }
        }
    }
    return error;
}

//input chỉ nhập số
function isInputNumber(evt) {
    var ch = String.fromCharCode(evt.which);
    if (!/[0-9]/.test(ch)) {
        evt.preventDefault();
    }
}

//Thay đổi đường dẫn ảnh bằng input file
function readURL(input1, input2) {
    if (input1.files && input1.files[0]) {
        var reader = new FileReader();
        reader.onload = function (e) {
            input2.attr("src", e.target.result);
        };
        reader.readAsDataURL(input1.files[0]);
    }
}

function reloadTableVariant(productId){
    if (productId === null || productId === "" || productId === undefined) {
        var url = "/manager/product/variants";
    } else {
        var url = "/manager/product/variants?productId=" + productId;
    }
    $.ajax({
        type: "Get",
        url: url,
        success: function (response) {
            var tbody = document.getElementById("tBodyProductVariant");
            while (tbody.hasChildNodes()) {
              tbody.removeChild(tbody.firstChild);
            }
            for (var i = 0; i < response.length; i++) {
              var data =
                  "<tr><td><input type=\"color\" class=\" \" value=\"" + response[i].color + "\" disabled>" +
                  "</td><td>" +
                  response[i].size +
                  "</td><td>" +
                  response[i].quantity +
                  "</td><td>" +
                  response[i].sku +
                  "</td><td><img id='img-product1-" +
                  response[i].sku +
                  "' class='img-product rounded' ";
                  if (response[i].image1 !== null) {
                      data += "src='/img/" + response[i].image1 + "'";
                  }
                  data += " alt=''></td> <td><img id='img-product2-" + response[i].sku + "' class='img-product rounded' ";
                  if (response[i].image2 !== null) {
                      data += "src='/img/" + response[i].image2 + "'";
                  }
                  data += " alt=''></td>" +
                  "<td><button type='button' class='btn btn-primary' data-toggle='modal'" +
                              "data-target=\"#modalEditVariant" + response[i].sku + "\">Sửa" +
                      "</button>" +
                "<!--Modal sửa variant-->" +
                  "<div class='modal fade bd-example-modal-lg' tabindex='-1' role='dialog'" +
                       "aria-labelledby='myLargeModalLabel' aria-hidden='true'" +
                       "id=\"modalEditVariant" + response[i].sku + "\">" +
                    "<div class='modal-dialog modal-lg'>" +
                      "<div class=\"modal-content\">" +
                        "<div class=\"container-fluid p-5 my-5\">" +
                          "<h4 class=\"text-uppercase text-danger text-center\">Sửa biến thể sản phẩm</h4>" +
                          "<hr/>" +
                          "<div>" +
                            "<div class=\"form-group\">" +
                              "<label class=\"font-weight-bold\">Màu sắc: <input type=\"color\" class=\" \" value=\"" + response[i].color + "\" disabled>" +
                              "</label>" +
                            "</div>" +
                            "<div class=\"form-group\">" +
                              "<label class=\"font-weight-bold\">Kích thước: <span" +
                                  " class=\"text-danger\" >" + response[i].size + "</span> </label>" +
                            "</div>" +
                            "<div class=\"form-group\">" +
                              "<label class=\"font-weight-bold\">SKU: <span" +
                                  " class=\"text-danger\">" + response[i].sku + "</span> </label>" +
                            "</div>" +
                            "<div class=\"form-group\">" +
                              "<label for=\"quantityVariant-" + response[i].sku + "\"" +
                                     "class=\"font-weight-bold\">Số lượng <span" +
                                  " class=\"text-danger\">*</span>" +
                                "<p id=\"error-quantity-variant-" + response[i].sku + "\"" +
                                   "class=\"error\"></p>" +
                              "</label>" +
                              "<input class=\"form-control\" type=\"number\"" +
                                     "onkeypress=\"isInputNumber(event)\" min=\"0\"" +
                                     "id=\"quantityVariant-" + response[i].sku + "\"" +
                                     "value=\"" + response[i].quantity + "\"" +
                                     "name=\"quantityVariant\" required/>" +
                            "</div>" +
                            "<div class=\"row\">" +
                              "<div class=\"col-md-6 col-12\">" +
                                "<p class=\"m-1\">Ảnh 1</p>" +
                                "<img src=\"/img/" + response[i].image1 + "\" alt=\"\"" +
                                     "class=\"img-variant rounded\"" +
                                     "id=\"img1-variant-" + response[i].sku + "\">" +
                                "<input type=\"file\" class=\"mt-3\"" +
                                       "id=\"fileVariant1-" + response[i].sku + "\"" +
                                       "onchange=\"readURL(this, $('#img1-variant-" + response[i].sku + "'))\"/>" +
                              "</div>" +
                              "<div class=\"col-md-6 col-12\">" +
                                "<p class=\"m-1\">Ảnh 2</p>" +
                                "<img src=\"/img/" + response[i].image2 + "\" alt=\"\"" +
                                     "class=\"img-variant rounded\"" +
                                     "id=\"img2-variant-" + response[i].sku + "\">" +
                                "<input type=\"file\" class=\"mt-3\"" +
                                       "id=\"fileVariant2-" + response[i].sku + "\"" +
                                       "onchange=\"readURL(this, $('#img2-variant-" + response[i].sku + "'))\"/>" +
                              "</div>" +
                            "</div>" +
                            "<button type=\"button\" class=\"btn btn-info mt-4\"" +
                              "onclick=\"editVariant(" + productId + ", '" + response[i].sku + "', $('#quantityVariant-" + response[i].sku + "').val(), $('#fileVariant1-" + response[i].sku + "')[0].files[0], $('#fileVariant2-" + response[i].sku +"')[0].files[0])\">Sửa" +
                            "</button>" +
                            "<button type=\"button\" class=\"btn btn-secondary mt-4 ml-1\" data-dismiss=\"modal\">Hủy" +
                            "</button>" +
                          "</div>" +
                        "</div>" +
                      "</div>" +
                    "</div>" +
                  "</div>" +
                "</td>" +
                "<td class=\"px-0\">" +
                  "<button type=\"button\" class=\"btn btn-warning\" data-toggle=\"modal\"" +
                          "data-target=\"#modalDeleteVariant" + response[i].sku + "\">Xóa" +
                  "</button>" +
                  "<!--                  Modal xóa variant-->" +
                  "<div class=\"modal fade\" id=\"modalDeleteVariant" + response[i].sku + "\"" +
                       "tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"exampleModalLabel\" aria-hidden=\"true\">" +
                    "<div class=\"modal-dialog\" role=\"document\">" +
                      "<div class=\"modal-content\">" +
                        "<div class=\"modal-header\">" +
                          "<h5 class=\"modal-title\" id=\"exampleModalLabel\">Sản phẩm</h5>" +
                          "<button type=\"button\" class=\"close\" data-dismiss=\"modal\"" +
                                  "aria-label=\"Close\"><span aria-hidden=\"true\">&times;</span>" +
                          "</button>" +
                        "</div>" +
                        "<div class=\"modal-body\">Bạn có muốn xóa sản phẩm màu: <input type=\"color\" class=\" \" value=\"" + response[i].color + "\" disabled>" + ", size: <span" +
                            " class=\"text-danger\">" + response[i].size + "</span> không??" +
                        "</div>" +
                        "<div class=\"modal-footer\">" +
                          "<button type=\"button\" class=\"btn btn-secondary\" data-dismiss=\"modal\">Hủy" +
                          "</button>" +
                          "<button type=\"button\" class=\"btn btn-primary\"" +
                                  "onclick=\"deleteVariantInSession(" + productId + ",'" + response[i].sku + "')\">Có" +
                          "</button>" +
                        "</div>" +
                      "</div>" +
                    "</div>" +
                  "</div>" +
                "</td>" +
              "</tr>";
              $("#tableProductVariant > tbody").append(data);
            }
        },
        error: function (jqXHR, exception, errorThrown) {
            if(jqXHR.status === 404 || errorThrown == 'Not Found'){
                window.location.href = "/manager/products";
            }
        },
        async: false,
    });
}

function addVariant2(productId) {
    var sKU = $("#productSKU").val().trim();
    var color = $("#productColor").val().trim();
    var size = $("#productSize").val().trim();
    var quantity = $("#productQuantity").val().trim();
    var error = checkVariant(color, size, sKU, quantity, productId);
    if (error === null) {
        var files1 = $("#product-image1")[0].files[0];
        var files2 = $("#product-image2")[0].files[0];
        var fd = new FormData();
        if(files1 !== undefined && files1 !== null){
            fd.append("file1", files1);
        }
        if(files2 !== undefined && files2 !== null){
            fd.append("file2", files2);
        }
        fd.append("color", color);
        fd.append("size", size);
        fd.append("quantity", quantity);
        fd.append("sKU", sKU);
        if (productId === null || productId === "" || productId === undefined) {
            var url = "/manager/product/variant";
        } else {
            var url = "/manager/product/variant?productId=" + productId;
        }
        $.ajax({
            type: "POST",
            encType: "multipart/form-data",
            url: url,
            cache: false,
            data: fd,
            contentType: false,
            processData: false,
            success: function (response) {
              reloadTableVariant(productId);
              $("#errorVariant").text("");
            },
            error: function (jqXHR, exception, errorThrown) {
                if(jqXHR.status === 400){
                  $("#errorVariant").text(jqXHR.responseText);
                }
                if(jqXHR.status === 404 || errorThrown == 'Not Found'){
                    window.location.href = "/manager/products";
                }
            },
            async: false,
        });
    }
    else{
      $("#errorVariant").text(error);
    }
}

function editVariant(productId, sKU, quantity, file1, file2){
  if(quantity === ""){
    $("#error-quantity-variant-" + sKU).text("Vui lòng nhập số lương");
    return;
  }else if(quantity >= 0){
    $("#error-quantity-variant-" + sKU).text("");
  }else{
    $("#error-quantity-variant-" + sKU).text("Số lượng không hợp lệ");
    return;
  }
  var fd = new FormData();
  if(file1 !== undefined && file1 !== null){
      fd.append("file1", file1);
  }
  if(file2 !== undefined && file2 !== null){
      fd.append("file2", file2);
  }
  fd.append("quantity", quantity);
  if (productId === null || productId === "" || productId === undefined) {
      var url = "/manager/product/variant/" + sKU ;
  } else {
      var url = "/manager/product/variant/" + sKU + "/?productId=" + productId;
  }
  $("#modalEditVariant" + sKU).modal("hide");
  $.ajax({
      type: "put",
      encType: "multipart/form-data",
      url: url,
      cache: false,
      data: fd,
      contentType: false,
      processData: false,
      success: function (response) {
         reloadTableVariant(productId);
         $("#error-quantity-variant-" + sKU).text("");
      },
      error: function (jqXHR, exception, errorThrown) {
          if(jqXHR.status === 400){
              $("#error-quantity-variant-" + sKU).text(jqXHR.responseText);
          }
          if(jqXHR.status === 404 || errorThrown == 'Not Found'){
              window.location.href = "/manager/products";
          }
      }
  });
}

function deleteVariantInSession(productId, sKU) {
    $("#modalDeleteVariant" + sKU).modal("hide");
    if (productId === null || productId === "" || productId === undefined) {
        var url = "/manager/product/variant/" + sKU ;
    } else {
        var url = "/manager/product/variant/" + sKU + "/?productId=" + productId;
    }
    $.ajax({
        type: "delete",
        url: url,
        success: function (response) {
           reloadTableVariant(productId);
        },
        error: function (jqXHR, exception, errorThrown) {
            if(jqXHR.status === 404 || errorThrown == 'Not Found'){
                window.location.href = "/manager/products";
            }
        }
    });
}

//Admin sửa trạng thái tài khoản khách hàng
function changeStatus(input, customerId) {
    if(input.checked){
        var statusActive = 1;
    }
    else{
        statusActive = 0;
    }
    $.ajax({
        type: "put",
        url: "/manager/customer/" + customerId,
        contentType: "application/json; charset=utf-8",
        dataType: "text",
        data: JSON.stringify({
          statusActive: statusActive
        }),
        success: function (response2) {
            $("#modal-content").text(response2);
            $('#modalChangeStatusCustomer').modal('show');
        },
         error: function (jqXHR, exception, errorThrown) {
             if(jqXHR.status === 400){
                $("#modal-content").text(jqXHR.responseText);
                $('#modalChangeStatusCustomer').modal('show');
             }
             if(jqXHR.status === 404){
                 window.location.href = "/manager/customers";
             }
         },
        async: false
    });
}

//Admin sửa trạng thái đơn hàng của khách hàng
function changeOrderStatus(orderStatus, orderId) {
    $.ajax({
        type: "put",
        url: "/manager/order/" + orderId,
        contentType: "application/json; charset=utf-8",
        dataType: "text",
        data: JSON.stringify({
            orderStatus: orderStatus,
        }),
        success: function (response) {
            $("#modal-content").text("Sửa trạng thái đơn hàng thành công");
            $("#modal").modal("show");
        },
        error: function (jqXHR, exception, errorThrown) {
            if(jqXHR.status === 400){
                var errors = JSON.parse(jqXHR.responseText);
                for(var i = 0; i < errors.length; i++){
                    if(errors[i].field === "orderStatus"){
                        $("#modal-content").text(errors[i].defaultMessage);
                        $("#modal").modal("show");
                    }
                }
            }
            if(jqXHR.status === 404){
                 window.location.href = "/manager/orders";
            }
        },
    });
}

//Xóa sản phẩm
function deleteProduct(productId) {
    $("#modalDeleteProduct" + productId).modal("hide");
    $.ajax({
        type: "delete",
        url: "/manager/product/" + productId,
        success: function (response) {
            $("#modal-content-message").text(response);
            $("#modalMessageDeleteProduct").modal("show");
        },
        error: function (jqXHR, exception, errorThrown) {
          if(jqXHR.status === 500){
              $("#modal-content-message").text("Lỗi hệ thống. Vui lòng thử lại sau");
              $("#modalMessageDeleteProduct").modal("show");
          }
        },
    });
}

function requestProductFilter() {
    var url = new URL("http://localhost:8080" + window.location.pathname);
    var key = new URLSearchParams(window.location.search).get("key");
    if(key != null){
      url.searchParams.append('key', key);
    }
    var category = $("input[name='filterCategory']:checked").val();
    var price = $("input[name='filterPrice']:checked").val();
    if (category != null && category.trim() !== "") {
        url.searchParams.append('category', category);
    }
    if (price === "0-200000") {
      url.searchParams.append('minPrice', 0);
      url.searchParams.append('maxPrice', 200000);
    } else if (price === "200000-400000") {
      url.searchParams.append('minPrice', 200000);
      url.searchParams.append('maxPrice', 400000);
    } else if (price === "400000-600000") {
      url.searchParams.append('minPrice', 400000);
      url.searchParams.append('maxPrice', 600000);
    } else if (price === "600000-800000") {
      url.searchParams.append('minPrice', 600000);
      url.searchParams.append('maxPrice', 800000);
    } else if (price === "800000-1200000") {
      url.searchParams.append('minPrice', 800000);
      url.searchParams.append('maxPrice', 1200000);
    } else if (price === "1200000-1800000") {
      url.searchParams.append('minPrice', 1200000);
      url.searchParams.append('maxPrice', 1800000);
    }
    window.location.href = url;
}

function uncheck(radio) {
    radio.prop('checked', false);
}

function loadGraph(data){
  var xValues = [];
  var yValues = [];
  for (let i = 0; i < data.length; i++){
    xValues.push(data[i].date);
    yValues.push(data[i].revenue);
  }

  new Chart("myChart", {
    type: "line",
    data: {
      labels: xValues,
      datasets: [{
        data: yValues,
        borderColor: "red",
        fill: true,
        label: 'Doanh thu',
      }]
    },
    options: {
      legend: {display: true}
    }
  });
}

function revenueStatistics(){
  var x = document.getElementById("dateTimeRevenue").value;
  window.location.href = "/manager/order/doanhthu?date=" + x;
}



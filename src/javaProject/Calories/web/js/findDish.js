function newXMLHttpRequest() {
  var xmlreq = false;
  if (window.XMLHttpRequest) {
    // Создадим XMLHttpRequest объект для не-Microsoft браузеров
    xmlreq = new XMLHttpRequest();
  } else if (window.ActiveXObject) {
    // Создадим XMLHttpRequest с помощью MS ActiveX
    try {
      // Попробуем создать XMLHttpRequest для поздних версий
      // Internet Explorer
      xmlreq = new ActiveXObject("Msxml2.XMLHTTP");
    } catch (e1) {
      // Не удалось создать требуемый ActiveXObject
      try {
        // Пробуем вариант, который поддержат более старые версии
        //  Internet Explorer
        xmlreq = new ActiveXObject("Microsoft.XMLHTTP");
      } catch (e2) {
        // Не в состоянии создать XMLHttpRequest с помощью ActiveX
      }
    }
  }
  return xmlreq;
}

var toolbar = document.querySelector('.toolbar--find_dish');
var control = document.querySelector('.toolbar-submit--find_dish');
var dishList = document.querySelector('.item_list');

function getSelectElements() {
    return document.querySelectorAll('.toolbar-select--components');
}
function getDishComponents(){
	var selects = getSelectElements();
	var components = [];
	for (var i = 0; i < selects.length; i++){
		components.push(selects[i].value);
	}
	return components;

}
function getDish(){
	var dish = {};
	var dishName = toolbar.querySelector('.toolbar-select--name').value;
	var dishComponents = getDishComponents();
	dish.dishName = dishName;
	dish.names = dishComponents;
	return dish;
}
//setDishes([{name: 'apple pie', components: [{id: 1, name: 'pie'},{id: 2,name: 'apple'}]}]);
function setDishes(arr){
	var dishes = arr;
	for (var i = 0; i < dishes.length; i++){
		var dish = document.createElement('div');

		var dishName = document.createElement('h2');
		dishName.textContent = dishes[i].name;

		var dishComponents = document.createElement('ul');
		var dishComponentsList = dishes[i].components;
		
		for (var j = 0; j < dishComponentsList.length; j++){
			var component = document.createElement('li');
			component.textContent += dishComponentsList[j].id + '.' + dishComponentsList[j].name;
			dishComponents.appendChild(component);
		}
		dish.appendChild(dishName);
		dish.appendChild(dishComponents);

		dishList.appendChild(dish);
	}
}
function findDish(_dish){
	var dishes = [];
	var dish = JSON.stringify(_dish);
	var xhr = new newXMLHttpRequest();
	xhr.open('POST', 'FindDish', true);
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xhr.send(dish);
	xhr.onreadystatechange = function(){
		if (this.readyState == 4 && this.status == 200){
            dishes = JSON.parse(xhr.responseText);
            setDishes(dishes);
        }
	}
}

control.addEventListener('click',function(){
	var dish = getDish();
	findDish(dish);
});
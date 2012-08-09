
$.fn.serializeObject = function() {
    var o = Object.create(null),
        elementMapper = function(element) {
            element.name = $.camelCase(element.name);
            return element;
        },
        appendToResult = function(i, element) {
            var node = o[element.name];

            if ('undefined' != typeof node && node !== null) {
                o[element.name] = node.push ? node.push(element.value) : [node, element.value];
            } else {
                o[element.name] = element.value;
            }
        };

    $.each($.map(this.serializeArray(), elementMapper), appendToResult);
    return o;
};

var createJSONProperty=function(result, pathElts, value){
	var property=pathElts.shift();
	if(property){		
		var matches=property.match(/\[\d+]$/)
		if(matches){
			var prop=property.replace(matches[0],"")
			var index=property.replace(prop,"").match(/\d+/)[0]			
			if(!(prop in result)){
				var subresult=createJSONProperty({}, pathElts,value);
				result[prop]=[subresult];
			}else{
				if(result[prop][index]){
					result[prop][index]=createJSONProperty(result[prop][index], pathElts,value);
				}else{				
					var subresult=createJSONProperty({}, pathElts,value)
					result[prop][index]=subresult	
				}
			}
			return result
		}else{
			if(!(property in result)){
				result[property]=createJSONProperty({}, pathElts, value)
			}else{
				result[property]=createJSONProperty(result[property], pathElts, value)
			}
			return result		
		}
	}else{
 		return value
	}
}
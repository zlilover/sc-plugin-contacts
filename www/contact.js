var exec = require("cordova/exec");
module.exports = {
	getContactOrAppList: function(content,successCallback, errorCallback){
		exec(
		successCallback,
		errorCallback,
		"SCPluginContact",//feature name
		"getContactOrAppList",//action
		content//要传递的参数，jsonarray格式
		);
	}
}

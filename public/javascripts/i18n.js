define(["i18n!nls/translations"], function(bundle) {

    var I18N = function(bundle) {

        this.bundle = bundle;

        this.t = function(key) {
            return this.bundle[key] || "?" + key + "?";
        }
    };

    var i18n = window.i18n = new I18N(bundle); // need global scope for templates
    return i18n;

});
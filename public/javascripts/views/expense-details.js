define([
    'zepto',
    'Underscore',
    'Backbone',
    'models/expense',
    'text!../../tpl/expense-detail.html'
], function ($, _, Backbone, Expense, template) {
    var serverDate=function(date){
        return date.toISOString().substr(0,10);
    } ;
    var ExpenseDetailsView = Backbone.View.extend({
        events:{
            "click #put":"saveExpense",
            "click #home":"close",
            "change header":"change",
            "change footer":"change",
            "click tr[name='line']":"editLine",
            "click #save_line":"saveLine",
            "change #line_form" :"changeLine"
        },
                         /*
                          [_.clone({
                          expense: 0.0,
                          description: " ",
                          valueDate: serverDate(new Date()),
                          expenseType: "Lodging",
                          account: "xebia"
                          })]
                         * */
        editLine:function(event){
            target=event.currentTarget;
            index=target.attributes['data-id'].nodeValue;
            this.model.currentLine=this.model.get('lines')[index];
            this.render();
        },
        changeLine:function(event){
            target = event.target;
            path = target.name.split('.');
            path.shift();
            value=target.value;
            if(target.type=="number"){
                value=(+target.value);
            }
            this.setValue(this.model.currentLine, path.shift(),path,value);
        },
        change:function(event){
            // Apply the change to the model
            target = event.target;
            path = target.name.split('.');

            value=target.value;
            if(target.type=="number"){
                value=(+target.value);
            }
            this.setValue(this.model.attributes, path.shift(),path,value);
        },
        setValue:function(object,key,path,value){
            var nextKey=path.shift();
            if(nextKey){
                setValue(object[key],nextKey, path, value);
            } else {
                object[key]=value;
            }
        },
        initialize:function () {
            this.slot=this.options.slot;
            this.model.on("reset", this.render, this);
            this.model.on("sync", this.render, this);
            this.model.on("change", this.render, this);
        },

        renderTemplate:function (json) {
            $expenseElement = $(_.template(template, json));
            $expenseElement.find('[name*="line.expenseType"]')[0].value=this.model.currentLine.expenseType;
            return $expenseElement;
        },

        render:function () {
            json = this.model.toJSON();
            json.currentLine=this.model.currentLine;
            this.$el.html(this.renderTemplate(json));
            this.slot.html(this.el);
            return this;
        },

        saveExpense:function () {
            this.model.save();
            return false;
        },

        saveLine:function () {
            this.model.saveCurrentLine();
            this.render();
        },

        close:function () {
            this.$el.unbind();
            this.$el.empty();
        }

    });
    return ExpenseDetailsView;
});

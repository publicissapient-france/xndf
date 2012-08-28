//add empty evidences array to each expense line
db.expenses.find().forEach(function(data) {
    data._lines=data._lines.map(function(line) {line.evidences=[];return line;});
    db.expenses.save(data);
});
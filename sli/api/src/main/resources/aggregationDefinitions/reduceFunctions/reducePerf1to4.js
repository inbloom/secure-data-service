
function(key,values) {
    
    //initialize sum variables to zero
    var level1total = level2total = level3total = level4total = 0;
    
    //for each value provided
    for( var i=0; i<values.length; i++) {    
        //add each value to sum
        level1total+=values[i].level1;
        level2total+=values[i].level2;
        level3total+=values[i].level3;
        level4total+=values[i].level4;
    }
    
    //return sum document
    return {level1:level1total,level2:level2total,level3:level3total,level4:level4total}; 
};

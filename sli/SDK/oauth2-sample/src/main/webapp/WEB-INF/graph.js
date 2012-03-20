function graph(canvas, data) {
	var ctx = canvas.getContext("2d");
	
	//x axis
	ctx.beginPath();
	ctx.moveTo(0, canvas.height);
	ctx.lineTo(canvas.width, canvas.height);
	ctx.closePath();
	ctx.stroke();
	
	//y axis
	ctx.beginPath();
	ctx.moveTo(0, 0);
	ctx.lineTo(0, canvas.height);
	ctx.closePath();
	ctx.stroke();
	console.log(data);
	var margin = 4;
	var samples = Object.keys(data).length;
	var barWidth = Math.round(canvas.width / 10) - margin;
	var currentX = margin;
	var counts = [0,0,0,0,0,0,0,0,0,0];
	for (var i in data) {
		var grade = data[i];
		console.log(Math.round(grade / 10) - 1);
		counts[Math.round(grade / 10)]++
	}
	
	var maxCount = 0;
	for (var i = 0; i < counts.length; i++) {
		if (counts[i] > maxCount) {
			maxCount = counts[i];
		}
	}
	ctx.fillStyle = "black"; 
	console.log(counts);
	for (var i = 0; i < counts.length; i++) {
		var height = canvas.height * counts[i] / maxCount;
		ctx.fillRect(currentX, canvas.height , barWidth, - Math.round(height));
		currentX += barWidth + margin;
	}
	/*for (var i in data) {
		var name = i;
		var grade = data[i];
		var height = canvas.height * grade / 100;
		ctx.fillStyle = "black"; 
		ctx.fillRect(currentX, canvas.height , barWidth, - Math.round(height));
		//console.log(currentX + "," + canvas.heightMath.round(canvas.height) + "," + barWidth + "," + height);
		currentX += barWidth + margin;
		
		//ctx.fillRect(25,25,63,243);  
	    //ctx.clearRect(45,45,60,60);  
	    //ctx.strokeRect(50,50,50,50); 
	}*/
	console.log(i);
}
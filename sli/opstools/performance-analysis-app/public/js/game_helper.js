var student_ids = new Array("1234", "2345", "3456");

if (window.File && window.FileReader && window.FileList && window.Blob) {
    // Yay
} else {
    alert('The File APIs are not fully supported in this browser.');
}

function handleFileSelect(evt) {
    var f = evt.target.files[0];

    var output = [];
    var reader = new FileReader();

    reader.onload = (function(theFile) {
        return function(e) {
            output.push(e.target.result);
        };
    })(f);

    reader.readAsText(f);
    document.getElementById('list').innerHTML = '<ul>' + output.join('') + '</ul>';
}
document.getElementById('fileinput').addEventListener('change', handleFileSelect, false);
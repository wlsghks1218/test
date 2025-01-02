<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Home</title>
</head>
<body>
<h1>
	Hello world!  
</h1>
<span id="test">test</span>
<P>  The time on the server is ${serverTime}. </P>
</body>
<script type="text/javascript">
	document.getElementById("test").addEventListener('click', ()=>{
		fetch('/api/test')
		.then(response => response.json())
		.then(data => console.log(data));
	})
</script>
</html>

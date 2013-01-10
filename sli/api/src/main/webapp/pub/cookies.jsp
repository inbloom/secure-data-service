<%
System.out.println("COOKIES");
if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                System.out.println(("Cookie " + c.getName() + "->" + c.getValue()));
            }
        }
%>

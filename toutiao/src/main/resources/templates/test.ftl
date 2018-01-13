
<#list colors as color>
    ${color} <br>
</#list>

<#list maps?keys as key>
    ${key} : ${maps[key]} <br>
</#list>

<#include "header.ftl">
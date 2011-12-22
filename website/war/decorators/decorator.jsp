<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html>
    <head>
        <title><decorator:title /> -- Caelyf - a lightweight Groovy toolkit for Cloud Foundry</title>

        <link rel="stylesheet" type="text/css" href="/css/main.css"/>

        <link rel="stylesheet" type="text/css" media="print" href="/css/print.css" />

    	<link type="text/css" rel="stylesheet" href="/css/shCore.css"/>
    	<link type="text/css" rel="stylesheet" href="/css/shThemeDefault.css"/>

        <script type="text/javascript" src="/js/jquery-1.3.2.min.js"></script>

        <script language="javascript" src="/js/shCore.js"></script>
        <script language="javascript" src="/js/shBrushGroovy.js"></script>
        <script language="javascript" src="/js/shBrushXml.js"></script>

        <script type="text/javascript">
        	SyntaxHighlighter.config.clipboardSwf = '/falsh/clipboard.swf';
        	SyntaxHighlighter.defaults['light'] = true;
        	SyntaxHighlighter.all();
        </script>

        <script type="text/javascript">
            var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
            document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
        </script>

        <script type="text/javascript">
            try {
                var pageTracker = _gat._getTracker("UA-257558-6");
                pageTracker._trackPageview();
            } catch(err) {}
        </script>

        <meta name="keywords" lang="en" content="caelyf, cloud foundry, groovy, java, cloud, grails, dynamic language, jvm">
    </head>

    <body>
        <div id="header">
            <a href="/" alt="Home"><img src="/images/gaelyk.png" alt="Caelyf - a lightweight Groovy toolkit for Cloud Foundry"></a>
        </div>

        <div id="navigation">
            <ul>
                <li><a href="/tutorial">Tutorial</a></li>
                <li><a href="/download">Download</a></li>
                <li><a href="/community">Community</a></li>
                <li><a href="/search">Search</a></li>
            </ul>
        </div>

        <div id="content">
            <decorator:body />
        </div>

        <div id="footer">
            <p>&copy; 2009-2011 &mdash; <b>Guillaume Laforge</b></p>
            <p>Powered by <b>Caelyf</b> and <b>Groovy</b></p>
        </div>
    </body>
</html>

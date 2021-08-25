package com.microyu.server;

import com.microyu.server.http.Cookie;
import com.microyu.server.http.Request;
import com.microyu.server.http.ResourceHandler;
import com.microyu.server.http.Response;
import com.microyu.server.servelet.ServletContext;
import com.microyu.server.utils.HttpRequestMethod;
import com.microyu.server.utils.HttpStatus;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

public class Dispacher implements Runnable {
    private static int PORT = 9999;

    private ServerSocket serverSocket;
    private Socket socket;
    private Request request;
    private Response response;

    public Dispacher() {
        try {
            this.serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                socket = serverSocket.accept();

                request = new Request(socket.getInputStream());
                //System.out.println(request.getRequestMessage());
                System.out.println(request.getUrl());

                response = new Response(socket.getOutputStream());
                response.appendCookie(new Cookie("JSESSIONID", request.getSession().getId()));
                //response.appendContent("<h2>HelloWolrd</h2>");

                //System.out.println(response.getResponseMessage());
                if (request.getMethod() == HttpRequestMethod.GET && (request.getUrl().contains(".") || request.getUrl().equals("/"))) {
                    if (request.getUrl().equals("/")) {
                        request.setUrl("/index.html");
                        ResourceHandler.handler(request, response);
                    } else {
                        ResourceHandler.handler(request, response);
                    }
                } else {
                    //处理动态资源，交由某个Servlet
                    response.appendContent("Servlet".getBytes(), 1024);
                }
                response.pushResponse(HttpStatus.OK);

                request.close();
                response.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

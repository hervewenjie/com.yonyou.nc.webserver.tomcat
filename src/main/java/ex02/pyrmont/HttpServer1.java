package ex02.pyrmont;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * you can process servlet resources use http://localhost:8080/servlet/ex02.pyrmont.PrimitiveServlet
 * that will invoke servlet.service method, but you should add .class file to webroot folder first.
 * @author mazhiqiang
 * @date 14-3-11.
 *
 * 本章建立的 servlet容器是一个很小的容器, 没有实现所有功能, 因此只能运行非常简单的servlet
 * 也 不会调用servlet init()和destroy()方法
 * 它做下面几件事情:
 * 1. 等待HTTP请求
 * 2. 创建servletRequest 和 servletResponse对象
 * 3. 请求static和servlet -> 若请求servlet, 则载入servlet类, 调用其service方法
 */
public class HttpServer1 {
	public static final String SHUTDOWN = "/shutdown";
	private boolean shutdown = false;

	public void await() {
		ServerSocket serverSocket = null;
		int port = 8080;
		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		while (!shutdown) {
			Socket socket = null;
			InputStream inputStream = null;
			OutputStream outputStream = null;

			try {
				socket = serverSocket.accept();
				inputStream = socket.getInputStream();
				outputStream = socket.getOutputStream();

				Request request = new Request(inputStream);
				request.parse();
				Response response = new Response(outputStream);
				response.setRequest(request);

				// 如果是 servlet
				if (request.getUri().startsWith("/servlet/")) {
					ServletProcessor1 processor1 = new ServletProcessor1();
					processor1.process(request, response);
				} else {
					StaticResourceProcessor staticResourceProcessor = new StaticResourceProcessor();
					staticResourceProcessor.process(request, response);
				}

				shutdown = request.getUri().equals(SHUTDOWN);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					inputStream.close();
					outputStream.close();
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		HttpServer1 httpServer1 = new HttpServer1();
		httpServer1.await();
	}
}

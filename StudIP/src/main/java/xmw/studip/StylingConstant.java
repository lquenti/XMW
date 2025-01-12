package xmw.studip;

public class StylingConstant {
    public static final String CSS = """
            /* geklaut von lars */
            html {
                background: #3366CC;
            }
            body {
                font-family: 'Trebuchet MS', Arial, sans-serif;
                background-color: #fff;
                border-radius: 20px;
                margin: 40px auto;
                max-width: 1200px;
                line-height: 1.6;
                font-size: 18px;
                color: #333;
                padding: 0 10px;
            }
            h1, h2, h3 {
                line-height: 1.2;
                text-align: center;
            }
            p, pre {
                margin: 0
            }
            footer img {
                display: block;
                margin: 10px auto;
            }
            .Payload, .Response {
                padding-left: 5px;
                margin-left: 20px;
                border-left: #3366CC 5px dotted;
            }
            .Response + .Response {
                margin-top: 5px;
            }
            """;
}

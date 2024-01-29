const fs = require("fs");
var pdf = require("dynamic-html-pdf");
const util = require("util");

module.exports = function generatePDF(TransXpedia) {
  return new Promise((resolved, rejected) => {
    var defaultfooter = "Page {{page}}";

    if (TransXpedia.CLIENT_JSON.hasOwnProperty("FOOTERVALUE")) {
      defaultfooter = TransXpedia.CLIENT_JSON.FOOTERVALUE + " Page {{page}}";
    } else {
      defaultfooter = "Page {{page}}";
    }

    var options = {
      format: "A4",
      border: {
        top: "10mm", // default is 0, units: mm, cm, in, px
        right: "10mm",
        bottom: "15mm",
        left: "10mm",
      },
      footer: {
        height: "5mm",
        contents: {
          default:
            '<div style="color: black;float:right;font-size:10px"><span>' +
            defaultfooter +
            "</span><span> of {{pages}}</span></div>",
        },
      },
    };
    var filePath = `./Utils/PDFCreator/${TransXpedia.CLIENT_JSON.TXNID}.pdf`;

    var document1 = {
      type: "buffer",
      template: TransXpedia.CLIENT_JSON.HTMLTEMPLATE,
      context: {
        jsonObj: TransXpedia.CLIENT_JSON.INPUTJSON,
      },
      path: filePath,
    };
    pdf
      .create(document1, options)
      .then((res) => {
        console.log("pdf generated");
        //	fs.writeFileSync(filePath, res);
        //	fs.unlinkSync(filePath);
        TransXpedia.CLIENT_JSON.PDFDATA = res.toString("base64");
        TransXpedia.CLIENT_JSON.ERRORCODE = "00";
        TransXpedia.errDescription = "SUCCESS";
        return resolved(TransXpedia);
      })
      .catch((error) => {
        console.log(error);
        TransXpedia.SERVER_MODULE = __filename;
        TransXpedia.errDescription = "PDF Generation Failed";
        TransXpedia.ErrorCode = "100";
        return rejected(TransXpedia);
      });
  });
};

var express = require('express');
var router = express.Router();

var gcloud = require('gcloud');

// Authenticating on a per-API-basis. You don't need to do this if you auth on a
// global basis (see Authentication section above).
var translate = gcloud.translate({
    key: 'AIzaSyBkl1DAD3S1iCMfjbeWqlTtUIn5Jxk0le4'
});

var options = {
    from: 'vi',
    to: 'ja'
};

/* GET users listing. */
router.get('/', function (req, res, next) {
    translate.translate("Tôi muốn đến Tokyo", options, function (error, result) {
        res.json({
            input: "Tôi muốn đến Tokyo",
            result: result
        });
    });
    // res.send('respond with a resource');
});

router.route('/translate').post(function (req, res) {
    var input = [
        req.body.text
    ];

    translate.translate(input, options, function (error, results, response) {
        res.json(response)
    })
});

// router.get('/translate', function (req, res) {
//     translate.translate(req, options, function (result) {
//         res.send(result);
//     });
// });

module.exports = router;
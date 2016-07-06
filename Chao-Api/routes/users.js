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
        res.send(result);
    });
    // res.send('respond with a resource');
});

// router.get('/translate', function (req, res) {
//     translate.translate(req, options, function (result) {
//         res.send(result);
//     });
// });

module.exports = router;

var express = require('express');
var router = express.Router();

var gcloud = require('gcloud');

// Authenticating on a per-API-basis. You don't need to do this if you auth on a
// global basis (see Authentication section above).
var translate = gcloud.translate({
    key: 'AIzaSyBkl1DAD3S1iCMfjbeWqlTtUIn5Jxk0le4'
});

/* GET users listing. */
router.get('/', function (req, res, next) {
    res.send('Respond with a resource');
});

router.route('/translate').post(function (req, res) {
    var input = [
        req.body.text
    ];

    var options = {
        from: req.body.from,
        to: req.body.to
    };

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
window.UmsApp =
window.EChat = {
    onDataLoad: function(fn) {
        this._bind_fn('umsapp://data/load', fn);
    },
    logout: function() {
        this._trigger_native(
            'umsapp://action/logout');
    },
	viewWorkbench: function() {
        this._trigger_native(
            'view://workbench');
    },
     titleGone: function() {
        var name = 'umsapp://action/view-settings';
        if (this._Android) {
            __android.receive(name,JSON.stringify({style:{header:{hide:true}}}));
                 
        }
        else if (this._iOS) {
//            document.location = name+'?style=' +encodeURI(JSON.stringify({style:{header:{hide:true}}}));
        	 this._trigger_native(
        	            'umsapp://action/view-settings?style='+JSON.stringify({header:{hide:true}}));
        }
    },
    titleVisible: function() {
        var name = 'umsapp://action/view-settings';
        if (this._Android) {
            __android.receive(name,JSON.stringify({style:{header:{hide:false}}}));
                 
        }
        else if (this._iOS) {
//            document.location = name+'?style=' +encodeURI(JSON.stringify({style:{header:{hide:false}}}));
        	this._trigger_native(
                    'umsapp://action/view-settings?style='+JSON.stringify({header:{hide:false}}));
        }
    },
    setViewSettings: function(data) {
        var name = 'umsapp://action/view-settings';
        if (this._Android) {
            __android.receive(name,data);
                 
        }
        else if (this._iOS) {
            document.location = name+'?style=' +encodeURI(data);
        }
    },
    startSoundRecord: function() {
        this._trigger_native(
            'umsapp://action/start-sound-record');
    },
    cancelSoundRecord: function(fn) {
        this._trigger_native(
            'umsapp://action/cancel-sound-record',
            'umsapp://data/cancel-sound-record', fn);
    },
    pauseSoundRecord: function(fn) {
        this._trigger_native(
            'umsapp://action/pause-sound-record',
            'umsapp://data/pause-sound-record', fn);
    },
    finishSoundRecord: function(fn) {
        this._trigger_native(
            'umsapp://action/finish-sound-record',
            'umsapp://data/finish-sound-record', fn);
    },
    removeSoundRecord: function() {
        this._trigger_native(
            'umsapp://action/remove-sound-record');
    },
    playSound: function(id) {
        this._trigger_native(
            'umsapp://action/play-sound?id=' + id);
    },
    pausePlaySound: function(id) {
        this._trigger_native(
            'umsapp://action/pause-play-sound?id=' + id);
    },
    stopPlaySound: function(id, fn) {
        this._trigger_native(
            'umsapp://action/stop-play-sound?id=' + id,
            'umsapp://data/play-sound-stopped', fn);
    },
    onPlaySoundStopped: function(fn) {
        this._bind_fn('umsapp://data/play-sound-stopped', fn);
    },
    uploadSound: function(id, fn) {
        this._trigger_native('umsapp://action/upload-sound?id=' + id, 
        	'umsapp://data/upload-sound', fn);
    },
    /**
     * callback: umsapp://action/location-setting, inverval单位ms,startTime,endTime 格式：yyyy-MM-dd hh:mm:ss
     */
    locationSetting: function(interval, startTime, endTime, fn) {
        var param = '?interval='+interval+'&startTime='+
                        encodeURI(startTime)+'&endTime='+encodeURI(endTime);
        this._trigger_native(
            'umsapp://action/location-setting' + param,
            'umsapp://data/location-setting' + param, fn);
    },
    /**
     * callback: umsapp://data/location, {x: 123, y: 456}
     */
    location: function(fn) {
        this._trigger_native(
            'action://location',
            'umsapp://data/location', fn);
    },
    /**
     * callback: umsapp://data/shake
     */
    shake: function(fn) {
        this._trigger_native(
            'umsapp://action/shake',
            'umsapp://data/shake', fn);
    },
    /**
     * callback: umsapp://data/date-pick
     */
    datePick: function(fn) {
        this._trigger_native(
            'umsapp://action/date-pick', 
            'umsapp://data/date-pick', fn);
    },
    /**
     * callback: umsapp://data/datetime-pick
     */
    datetimePick: function(fn) {
        this._trigger_native(
            'umsapp://action/datetime-pick', 
            'umsapp://data/datetime-pick', fn);
    },
    /**
     * callback: umsapp://data/time-pick
     */
    timePick: function(fn) {
        this._trigger_native(
            'umsapp://action/time-pick', 
            'umsapp://data/time-pick', fn);
    },
    /**
     * callback: umsapp://data/photo, http://www.flaginfo.com.cn/file/123.png
     */
    photo: function(fn) {
        this._trigger_native(
            'action://photo', 
            'umsapp://data/photo', fn);
    },
    /**
     * callback: umsapp://data/qr-scan, 123456
     */
    qrScan: function(fn) {
        this._trigger_native(
            'action://qr-scan', 
            'umsapp://data/qr-scan', fn);
    },
    /**
     * callback: umsapp://data/contact-select, params
     * params: [{
     *      "mobile": "131131",
     *      "name": "zhangfei",
     *      "gender": "F",
     *      "email": "foo@bar.com",
     *      "avatar": "http://umsapp.com/avatar/11133.png"
     * }, {
     *      "mobile": "131131",
     *      "name": "zhaoyun",
     *      "gender": "M",
     *      "email": "foo@bar.com",
     *      "avatar": "http://umsapp.com/avatar/11313.png"
     * }]
     */
    contactSelect: function(fn) {
        this._trigger_native(
            'action://contact-select', 
            'umsapp://data/contact-select', fn);
    },
    /**
     * callback: umsapp://data/upload-file, http://file/fooba.pdf
     */
    uploadFile: function(fn) {
        this.send('umsapp://action/upload-file');
        this._trigger_native(
            'umsapp://action/upload-file', 
            'umsapp://data/upload-file', fn);
    },
    createChatroom: function() {
        var args = Array.prototype.slice.call(arguments);
        var arg = args.shift(), 
            fn, 
            p = 'umsapp://action/chatroom/create';
        
        if (Array.isArray(arg)) {
            p += '?members=' + encodeURIComponent(JSON.stringify(arg));
            fn = args.shift();
        }
        else if (this._is_fn(arg)) {
            fn = arg;
        }
        this._trigger_native(
            p, 'umsapp://data/chatroom/create', fn);
    },
    send: function(name) {
        if (this._Android) {
            __android.receive(name);
                 
        }
        else if (this._iOS) {
            document.location = name;
        }
        else {
            console.error('Platform Not Supported!');
        }
    },
    receive: function(name, data) {
        var me = this;
        if (name) {

            // previous version compatible 
            if (name === 'data://location' || 
                name === 'umsapp://data/location') {
                data.x = data.x || data.locationX;
                data.y = data.y || data.locationY;
                delete data.locationX;
                delete data.locationY;
            }
            if (me[name]) {
                me[name](data);
            }
            else {

                $('.listener').trigger(name, [data]);
                me.callback && me.callback(name, data);
                // previous version compatible
                name = me._api_mapping[name];
                if (name) {
                    if (me[name]) {
                        me[name](data);
                    }
                    else {
                        $('.listener').trigger(name, [data]);
                        me.callback && me.callback(name, data);
                    }
                }
            }
        }
        else {
            alert('Param [name] Is Required.');
        }
    },
    _Android: navigator.userAgent.indexOf('Android') > -1,
    _iOS: !!navigator.userAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/),
    _api_mapping: {
        'data://location': 'umsapp://data/location', 
        'data://qr-scan': 'umsapp://data/qr-scan', 
        'data://contact-select': 'umsapp://data/contact-select',
        'data://photo': 'umsapp://data/photo'
    },
    _is_fn: function(obj) {
        return !!(obj && obj.constructor && obj.call && obj.apply);
    },
    _bind_fn: function(name, fn) {
        name && this._is_fn(fn) && (this[name] = fn);
    },
    _trigger_native: function(p, c, fn) {
        this.send(p);
        this._bind_fn(c, fn);
    }
};

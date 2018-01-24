/*@ngInject*/
export default function MissingTranslateHandler($log) {

    return function (translationId) {
        if (translationId && !translationId.startsWith("custom.")) {
            $log.warn('Translation for ' + translationId + ' doesn\'t exist');
        }
    };

}
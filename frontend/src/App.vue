<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=ZCOOL+KuaiLe&display=swap');

#app {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  background-color: #f5f5f5;
}

.pawfu-title {
  font-family: 'ZCOOL KuaiLe', 'Comic Sans MS', 'YouYuan', 'Microsoft YaHei', cursive, sans-serif;
  font-weight: 600;
  letter-spacing: 2px;
}

/* Element UI MessageBox 移动端适配 */
@media (max-width: 768px) {
  .el-message-box {
    width: 90% !important;
    max-width: 400px !important;
    margin-top: 10vh !important;
  }
  
  .el-message-box__header {
    padding: 12px 16px !important;
  }
  
  .el-message-box__title {
    font-size: 16px !important;
  }
  
  .el-message-box__content {
    padding: 16px !important;
    font-size: 14px !important;
  }
  
  .el-message-box__btns {
    padding: 12px 16px !important;
  }
  
  .el-message-box__btns button {
    flex: 1;
    margin: 0 4px !important;
  }
  
  /* Dialog 移动端适配 */
  .el-dialog {
    margin-top: 5vh !important;
  }
  
  .el-dialog__header {
    padding: 12px 16px !important;
  }
  
  .el-dialog__body {
    padding: 16px !important;
  }
  
  /* 表格移动端滚动 */
  .el-table {
    font-size: 13px !important;
  }
  
  .el-table__body-wrapper {
    overflow-x: auto !important;
  }
  
  /* 分页移动端适配 */
  .el-pagination {
    white-space: normal !important;
    text-align: center !important;
  }
  
  .el-pagination__sizes,
  .el-pagination__jump {
    display: none !important;
  }
  
  /* Select移动端适配 */
  .el-select {
    width: 100% !important;
    margin-bottom: 8px !important;
  }
  
  /* Card移动端适配 */
  .el-card__header {
    padding: 12px 16px !important;
  }
  
  .el-card__body {
    padding: 12px 16px !important;
  }
}
</style>

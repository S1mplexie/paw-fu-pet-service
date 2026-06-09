<template>
  <div class="my-pets-page">
    <div class="header">
      <div class="container">
        <h1 class="logo pawfu-title"><paw-icon :size="32" class="logo-icon"></paw-icon> Paw福宠物服务平台 - 我的发布</h1>
        <router-link to="/" class="back-btn">返回首页</router-link>
      </div>
    </div>
    
    <div class="container">
      <el-card>
        <el-table :data="petList" style="width: 100%">
          <el-table-column prop="petName" label="宠物名称" width="150"></el-table-column>
          <el-table-column prop="categoryName" label="种类" width="100"></el-table-column>
          <el-table-column label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.adoptionStatus)">
                {{ getStatusName(scope.row.adoptionStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="发布时间" width="180"></el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button size="small" @click="viewDetail(scope.row.petId)">查看</el-button>
              <el-button size="small" type="primary" @click="handleEdit(scope.row.petId)" v-if="scope.row.adoptionStatus !== 2">修改</el-button>
              <el-button size="small" type="warning" @click="handleOffline(scope.row.petId)" v-if="scope.row.adoptionStatus === 1">下架</el-button>
              <el-button size="small" type="success" @click="handleOnline(scope.row.petId)" v-if="scope.row.adoptionStatus === 3">上架</el-button>
              <el-button size="small" type="danger" @click="handleDelete(scope.row.petId)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script>
import { getMyPets, offlinePet, onlinePet, deletePet } from '@/api/pet'
import PawIcon from '@/components/PawIcon.vue'

export default {
  name: 'MyPets',
  components: {
    PawIcon
  },
  data() {
    return {
      petList: []
    }
  },
  mounted() {
    this.loadMyPets()
  },
  methods: {
    async loadMyPets() {
      try {
        const res = await getMyPets()
        this.petList = res.data.records || []
      } catch (error) {
        this.$message.error('加载失败')
      }
    },
    viewDetail(id) {
      this.$router.push(`/pet/${id}`)
    },
    handleEdit(id) {
      this.$router.push(`/publish/${id}`)
    },
    async handleOffline(id) {
      try {
        await this.$confirm('确定下架该领养信息？', '提示', {
          type: 'warning'
        })
        await offlinePet(id)
        this.$message.success('下架成功')
        this.loadMyPets()
      } catch (error) {
        if (error !== 'cancel') {
          console.error(error)
        }
      }
    },
    async handleOnline(id) {
      try {
        await this.$confirm('确定上架该领养信息？', '提示', {
          type: 'warning'
        })
        await onlinePet(id)
        this.$message.success('上架成功')
        this.loadMyPets()
      } catch (error) {
        if (error !== 'cancel') {
          console.error(error)
        }
      }
    },
    async handleDelete(id) {
      try {
        await this.$confirm('确定删除该领养信息？', '提示', {
          type: 'warning'
        })
        await deletePet(id)
        this.$message.success('删除成功')
        this.loadMyPets()
      } catch (error) {
        if (error !== 'cancel') {
          console.error(error)
        }
      }
    },
    getStatusName(status) {
      const map = { 1: '待领养', 2: '已领养', 3: '已下架' }
      return map[status] || '未知'
    },
    getStatusType(status) {
      const map = { 1: 'success', 2: 'info', 3: 'warning' }
      return map[status] || 'info'
    }
  }
}
</script>

<style scoped>
.my-pets-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.header .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-size: 24px;
  font-weight: bold;
}

.back-btn {
  color: white;
  text-decoration: none;
}
</style>

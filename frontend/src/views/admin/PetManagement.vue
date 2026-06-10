<template>
  <div class="pet-management">
    <el-card>
      <div slot="header" class="card-header">
        <span>宠物信息管理</span>
      </div>
      <div class="filter-bar">
        <el-select v-model="statusFilter" placeholder="领养状态" clearable style="width: 150px; margin-right: 10px">
          <el-option label="待领养" value="0"></el-option>
          <el-option label="已领养" value="1"></el-option>
        </el-select>
        <el-select v-model="categoryFilter" placeholder="宠物分类" clearable style="width: 150px; margin-right: 10px">
          <el-option label="狗狗" value="dog"></el-option>
          <el-option label="猫咪" value="cat"></el-option>
          <el-option label="其他" value="other"></el-option>
        </el-select>
        <el-button type="primary" icon="el-icon-search" @click="fetchPets">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetFilter">重置</el-button>
      </div>
      <el-table :data="petList" v-loading="loading" border stripe>
        <el-table-column prop="petId" label="宠物ID" width="120"></el-table-column>
        <el-table-column prop="petName" label="宠物名称" width="120"></el-table-column>
        <el-table-column prop="category" label="分类" width="100">
          <template slot-scope="scope">
            {{ getCategoryText(scope.row.category) }}
          </template>
        </el-table-column>
        <el-table-column prop="breed" label="品种" width="120"></el-table-column>
        <el-table-column prop="age" label="年龄" width="80"></el-table-column>
        <el-table-column prop="gender" label="性别" width="80">
          <template slot-scope="scope">
            {{ scope.row.gender === 'male' ? '公' : '母' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status || scope.row.adoptionStatus)" size="small">
              {{ getStatusText(scope.row.status || scope.row.adoptionStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publisherName" label="发布者" width="120"></el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180"></el-table-column>
        <el-table-column label="操作" width="240">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-view" @click="viewDetail(scope.row)">详情</el-button>
            <el-button
              v-if="(scope.row.status || scope.row.adoptionStatus) === 1"
              type="text"
              icon="el-icon-download"
              @click="handleOffline(scope.row)"
              style="color: #e6a23c"
            >下架</el-button>
            <el-button type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" style="color: #f56c6c">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        style="margin-top: 20px; text-align: right"
      ></el-pagination>
    </el-card>
    <el-dialog title="宠物详情" :visible.sync="detailVisible" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="宠物ID">{{ currentPet.petId }}</el-descriptions-item>
        <el-descriptions-item label="宠物名称">{{ currentPet.petName }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ getCategoryText(currentPet.category) }}</el-descriptions-item>
        <el-descriptions-item label="品种">{{ currentPet.breed }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ currentPet.age }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ currentPet.gender === 'male' ? '公' : '母' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentPet.status || currentPet.adoptionStatus)" size="small">
            {{ getStatusText(currentPet.status || currentPet.adoptionStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布者">{{ currentPet.publisherName }}</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ currentPet.createTime }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentPet.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ currentPet.description }}</el-descriptions-item>
        <el-descriptions-item label="图片" :span="2">
          <el-image
            v-for="(img, index) in currentPet.images"
            :key="index"
            :src="img"
            :preview-src-list="currentPet.images"
            style="width: 100px; height: 100px; margin-right: 10px"
            fit="cover"
          ></el-image>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { getPetList, getPetDetail, offlinePet, deletePet } from '@/api/admin'

export default {
  name: 'PetManagement',
  data() {
    return {
      petList: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      statusFilter: '',
      categoryFilter: '',
      detailVisible: false,
      currentPet: {}
    }
  },
  created() {
    this.fetchPets()
  },
  methods: {
    async fetchPets() {
      this.loading = true
      try {
        const res = await getPetList({
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          status: this.statusFilter,
          category: this.categoryFilter
        })
        this.petList = res.data.records || res.data.list || []
        this.total = res.data.total || 0
      } catch (error) {
        console.error(error)
      } finally {
        this.loading = false
      }
    },
    resetFilter() {
      this.statusFilter = ''
      this.categoryFilter = ''
      this.pageNum = 1
      this.fetchPets()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.fetchPets()
    },
    handleCurrentChange(val) {
      this.pageNum = val
      this.fetchPets()
    },
    getCategoryText(category) {
      const map = {
        dog: '狗狗',
        cat: '猫咪',
        other: '其他'
      }
      return map[category] || category
    },
    getStatusText(status) {
      const map = {
        1: '待领养',
        2: '已领养',
        3: '已下架'
      }
      return map[status] || '未知'
    },
    getStatusType(status) {
      const map = {
        1: 'success',
        2: 'info',
        3: 'warning'
      }
      return map[status] || 'info'
    },
    async viewDetail(row) {
      try {
        const res = await getPetDetail(row.petId)
        this.currentPet = res.data
        this.detailVisible = true
      } catch (error) {
        console.error(error)
      }
    },
    handleOffline(row) {
      this.$confirm('确定要强制下架该宠物信息吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await offlinePet(row.petId)
          this.$message.success('下架成功')
          this.fetchPets()
        } catch (error) {
          console.error(error)
        }
      }).catch(() => {})
    },
    handleDelete(row) {
      this.$confirm('确定要删除该宠物信息吗？此操作不可恢复！', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(async () => {
        try {
          await deletePet(row.petId)
          this.$message.success('删除成功')
          this.fetchPets()
        } catch (error) {
          console.error(error)
        }
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.pet-management {
  padding: 20px 0;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.filter-bar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}
</style>
